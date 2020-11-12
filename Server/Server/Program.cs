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
        public static string pageMethod = "/page-";
        public static string allAnimeMethod = "/anime";
        public static string url = "https://jut.su";
        public static string nunMethod = "";

        static void Main(string[] args)
        {
            if (args.Length == 0)
            {
                using (WebClient wc = new WebClient())
                {
                    string res = wc.DownloadString("http://192.168.0.110:25525/load?page=1");

                    Console.WriteLine(res + "d");
                    Console.ReadKey();
                }
            }
            else
            {
                string result = GetRequestString(allAnimeMethod + pageMethod + args[0]);
                List<InfoAnime> mas = GetLinkAnime(result, args[0]);
                SaveToDataBase(mas);
                Console.WriteLine("ok");
            }

        }

        public static string GetRequestString(string method)
        {
            HttpWebRequest request = (HttpWebRequest)WebRequest.Create(url + method);
            HttpWebResponse response = (HttpWebResponse)request.GetResponse();
            
            string data = "";
            if (response.StatusCode == HttpStatusCode.OK)
            {
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

        public static List<InfoAnime> GetLinkAnime(string str, string page)
        {
            HtmlParser parser = new HtmlParser();

            IHtmlDocument c =  parser.ParseDocument(str);
            //all_anime_image
            var link_anime = c.QuerySelectorAll("div")
                .Where(m => m.ClassName == "all_anime")
                .Select(e => { 
                    string key = e.ParentElement.GetAttribute("href");
                    string val = e.Children.Where(q => q.ClassName == "all_anime_image")
                        .Select(y => y.GetAttribute("style"))
                        .FirstOrDefault();
                    try
                    {
                        val = val.Split((char)39)[1];
                    }catch(Exception ex)
                    {
                        val = "null";
                    }
                    return new KeyValuePair<string, string>(key, val); 
                });


            List<InfoAnime> mas = new List<InfoAnime>();
            foreach (var r in link_anime)
            {
                string tmp = GetInfoAnime(GetRequestString(r.Key));
                mas.Add(new InfoAnime(r.Key, r.Value, tmp, page));
            }

            return mas;
        }

        public static string GetInfoAnime(string str)
        {
            HtmlParser parser = new HtmlParser();

            IHtmlDocument c = parser.ParseDocument(str);

            var link_anime = c.QuerySelectorAll("p").Where(e => e.ClassName == "under_video uv_rounded_bottom the_hildi").Select(m => m.Html()).FirstOrDefault();

            Regex reg = new Regex(@"\s?<i[^>]*?>.*?</i>\s?");
            Regex reg2 = new Regex(@"<[^>]+>|&nbsp");

            string tmp = reg2.Replace(reg.Replace(link_anime, " ").Replace("<br>", "\n"), " ").Trim();
            return tmp;
        }

        public static void SaveToDataBase(List<InfoAnime> mas)
        {

            try
            {
                string str = JsonConvert.SerializeObject(mas);

                string connect = ConfigurationManager.ConnectionStrings["toDataBase"].ConnectionString;
                NpgsqlConnection conn = new NpgsqlConnection(connect);
                conn.Open();

                string query = string.Format("call add_new_anime_info(\'{0}\');", str);

                NpgsqlCommand command = new NpgsqlCommand(query, conn);

                command.ExecuteNonQuery();

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
