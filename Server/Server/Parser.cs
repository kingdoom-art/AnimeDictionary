using AngleSharp.Dom;
using AngleSharp.Html.Dom;
using AngleSharp.Html.Parser;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net;
using System.Text;
using System.Text.RegularExpressions;
using System.Threading.Tasks;

namespace Server
{
    class Parser
    {
        public string baseUrl { get; } = "https://jut.su";
        public string allAnimeMethod { get; } = "/anime";
        public string pageMethod { get; } = "/page-";
        public string nunMethod { get; } = "";
        private DBConnection connection;

        private Parser() 
        {
            connection = new DBConnection();        
        }

        private static Parser singParser;

        public static Parser GetInstance()
        {
            singParser = singParser == null ? new Parser() : singParser;
            return singParser;
        }

        public string SavePage(string page)
        {
            bool flag;
            int _page;
            flag = int.TryParse(page, out _page);
            if (flag)
            {
                string tmp_res = GetRequestString(baseUrl+allAnimeMethod+ pageMethod + page);
                if (tmp_res != "error")
                {
                    //распарсили страничку - сформировали объекты
                    List<InfoAnime> mas = GetLinkAnime(tmp_res, page);
                    return connection.SaveListInfo(mas);
                }
                else
                {
                    return tmp_res;
                }
            }
            else
            {
                return ServerComand.GetInstance().Error("SavePage not int pagam page");
            }
        }

        private string GetRequestString(string url)
        {
            string data = "";
            try
            {
                //собственно коннектимся к сайту
                HttpWebRequest request = (HttpWebRequest)WebRequest.Create(url);
                //вернум ответ
                HttpWebResponse response = (HttpWebResponse)request.GetResponse();

                if (response.StatusCode == HttpStatusCode.OK)
                {
                    /*
                       суть такая что нужно получить поток для чтения ответа
                       как только получили читаем, проверка на кодировку с гугла была скопипащена
                       после все, что считали запихаем в переменную, а потом закроем потоки
                       можно переделать с использоавнием using, чтобы не закрывать потоки руками
                     */
                    Stream receiveStream = response.GetResponseStream();
                    StreamReader readStream;
                    if (response.CharacterSet == null)
                    {
                        readStream = new StreamReader(receiveStream);
                    }
                    else
                    {
                        readStream = new StreamReader(receiveStream, Encoding.GetEncoding(response.CharacterSet));
                    }
                    data = readStream.ReadToEnd();
                    response.Close();
                    readStream.Close();
                }
            }catch(Exception ex)
            {
                ServerComand.GetInstance().Error("GetRequestString "+ex.Message);
                data = "error";
            }       
            return data;
        }

        //преобразует html-разметку в нужные нам объекты
        private List<InfoAnime> GetLinkAnime(string str, string page)
        {
            //делаем парсер хтмлки
            HtmlParser parser = new HtmlParser();
            //делаем документ из него, чтобы доставать нужное нам
            IHtmlDocument c = parser.ParseDocument(str);

            //инфа об анимешках хранится в тэге div с классом all_anime, его то нам и нужно достать
            var link_anime = c.QuerySelectorAll("div")//берем все тэги div
                .Where(m => m.ClassName == "all_anime")//берем только по нужному нам классу
                .Select(e => { //тут нам нужно вывести инфу об аниме ссылку на его описание и картинку
                    //получить ссылку изи, она в том же тэге
                    string key = e.ParentElement.GetAttribute("href");
                    //картинка хранится в потомке div, по этому достаем его
                    string val = e.Children.Where(q => q.ClassName == "all_anime_image")//берем потомков с классом all_anime_image в нем хранится ссылка на анимешку
                        .Select(y => y.GetAttribute("style"))//но ссылка хранится в стиле, возьмем ее
                        .FirstOrDefault();//можно и без этого, но пусть будет
                    //теперь нужно ссылку достать из стиля, по скольку в стиле по мимо ссылки есть еще и левая инфа
                    try
                    {
                        //ссылка обрамлена одниарными кавыками. сплитим и берем второй элемент
                        //вводиу того, что я без понятия как делать экранирование одинарных кавычек считерим немного
                        //(char)39 - одинарная кавыка
                        val = val.Split((char)39)[1];
                    }
                    catch (Exception ex)
                    {
                        //если все пошно не по плану сделаем заглушку. что картинки  нет
                        val = "null";
                    }
                    //вернем паку ссылка на следующее аниме и ссылка на картинку
                    return new KeyValuePair<string, string>(key, val);
                });

            //делаем массив объектов. чтобы преобразовать в json потом, а не отправлять как алеша каждое аниме
            //в базу данных, проще отправить джейсон и отдыхать
            List<InfoAnime> mas = new List<InfoAnime>();
            //теперь все есть, кроме описания аниме
            //его тоже нужно взять со страницы аниме
            foreach (var r in link_anime)
            {
                //все просто, тырим инфу об анимешке оп нашей ссылке
                string tmp = GetInfoAnime(GetRequestString(baseUrl + r.Key));
                //все сохраняем, ссылка на аниме нужна в дальнейшем, чтобы открывать сайт с аниме из клиента
                //страница нужна чтобы не грузить все данные из таблицы в будущем, а постранично
                mas.Add(new InfoAnime(r.Key, r.Value, tmp, page));
            }

            return mas;
        }

        //получение описания аниме
        private string GetInfoAnime(string str)
        {
            //и снова здрасте, аналогично GetLinkAnime
            HtmlParser parser = new HtmlParser();

            IHtmlDocument c = parser.ParseDocument(str);

            //тырим описание аниме оно в тэге p с классом under_video uv_rounded_bottom the_hildi, такое чувство. что сайт писали
            //специально для парсинга
            var link_anime = c.QuerySelectorAll("p")
                .Where(e => e.ClassName == "under_video uv_rounded_bottom the_hildi")
                .Select(m => m.Html()).FirstOrDefault();//тут нам нужна хтмлка по скльку в тексте есть еще тэги

            //регулярку убирает i-тэги с их содержимым
            Regex reg = new Regex(@"\s?<i[^>]*?>.*?</i>\s?");
            //удаляем оставшиеся тэги, без содержимого
            Regex reg2 = new Regex(@"<[^>]+>|&nbsp");

            //регулярками все убрали (заменили пробелами), уберем лишние пробелы
            string tmp = reg2.Replace(reg.Replace(link_anime, " ").Replace("<br>", "\n"), " ").Trim();
            return tmp;
        }
    }
}
