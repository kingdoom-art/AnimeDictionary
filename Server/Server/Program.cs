using AngleSharp.Dom;
using AngleSharp.Html.Dom;
using AngleSharp.Html.Parser;
using Newtonsoft.Json;
using Npgsql;
using System;
using System.Collections.Generic;
using System.Configuration;
using System.IO;
using System.Linq;
using System.Net;
using System.Text;
using System.Text.RegularExpressions;

namespace Server
{
    class Program
    {
        //метод для получения следующей страницы с аниме
        public static string pageMethod = "/page-";
        //метод получения списка аниме
        public static string allAnimeMethod = "/anime";
        //сам ресурс
        public static string url = "https://jut.su";
        public static string nunMethod = "";

        static void Main(string[] args)
        {
            //заглушка для проерки клиента, когда клиента еще нет
            //если нет аргументов, то это клиент, иначе это сервер запустил процесс
            if (args.Length == 0)
            {
                //образаемся к серверу нужно для теста функционала сервера без реализации клиента
                using (WebClient wc = new WebClient())
                {
                    string res = wc.DownloadString("http://192.168.0.110:25525/load?page=6");

                    Console.WriteLine(res);
                    Console.ReadKey();
                }
            }
            else
            {
                //стырим разметку сайта для дальнейшей обработки и вернум строку
                string result = GetRequestString(allAnimeMethod + pageMethod + args[0]);
                //распарсили страничку - сформировали объекты
                List<InfoAnime> mas = GetLinkAnime(result, args[0]);
                //сожранение в бд
                SaveToDataBase(mas);
                Console.WriteLine("ok");
            }

        }

        //вернет ответ сайта в виде строки
        public static string GetRequestString(string method)
        {
            //собственно коннектимся к сайту
            HttpWebRequest request = (HttpWebRequest)WebRequest.Create(url + method);
            //вернум ответ
            HttpWebResponse response = (HttpWebResponse)request.GetResponse();
            
            string data = "";
            if (response.StatusCode == HttpStatusCode.OK)
            {
                /*
                   суть такая что нужно получить поток для чтения ответа
                   как только получили читаем, проверка на кодировку с гугла была скопипащена
                   после все, что считали запихаем в переменную, а потом закроем потоки
                   можно переделать с использоавнием using, чтобы не закрывать потоки руками
                 */
                Stream receiveStream = response.GetResponseStream();
                StreamReader readStream = null;
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
            return data;
        }

        //преобразует html-разметку в нужные нам объекты
        public static List<InfoAnime> GetLinkAnime(string str, string page)
        {
            //делаем парсер хтмлки
            HtmlParser parser = new HtmlParser();
            //делаем документ из него, чтобы доставать нужное нам
            IHtmlDocument c =  parser.ParseDocument(str);

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
                    }catch(Exception ex)
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
                string tmp = GetInfoAnime(GetRequestString(r.Key));
                //все сохраняем, ссылка на аниме нужна в дальнейшем, чтобы открывать сайт с аниме из клиента
                //страница нужна чтобы не грузить все данные из таблицы в будущем, а постранично
                mas.Add(new InfoAnime(r.Key, r.Value, tmp, page));
            }

            return mas;
        }

        //получение описания аниме
        public static string GetInfoAnime(string str)
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

        //сохранение в бд
        public static void SaveToDataBase(List<InfoAnime> mas)
        {
            //если коннект к базе не удался, отправим серверу инфу об этом
            try
            {
                //делаем джейсон
                string str = JsonConvert.SerializeObject(mas);
                //лполучаем строку подключения к бд из app.config
                string connect = ConfigurationManager.ConnectionStrings["toDataBase"].ConnectionString;
                //коннектимся
                NpgsqlConnection conn = new NpgsqlConnection(connect);
                conn.Open();
                //процедуркой заносим все в бд
                string query = string.Format("call add_new_anime_info(\'{0}\');", str);

                NpgsqlCommand command = new NpgsqlCommand(query, conn);

                command.ExecuteNonQuery();
                //чилим
                conn.Close();
            }catch(Exception ex)
            {
                using (WebClient wc = new WebClient())
                {
                    string res = wc.DownloadString("http://192.168.0.110:25525/error?error=" + ex.Message);
                }
            }
        }
    }
}
