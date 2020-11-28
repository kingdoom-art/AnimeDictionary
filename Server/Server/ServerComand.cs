using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Text;
using System.Threading.Tasks;

namespace Server
{
    class ServerComand
    {
        public string baseUrl { get; } = "http://192.168.0.110:25525";
        public string loadMethod { get; } = "/load";
        public string errorMethod { get; } = "/error";

        private static ServerComand singlServer;

        public static ServerComand GetInstance()
        {
            singlServer = singlServer == null ? new ServerComand() : singlServer;
            return singlServer;
        }

        //чтобы не плодить  такие классы сделаем его одиночкой
        private ServerComand()
        {

        }

        public string LoadPage(string page)
        {
            List<KeyValuePair<string, string>> args = new List<KeyValuePair<string, string>>();
            args.Add(new KeyValuePair<string, string>("page", page));
            string result = SendRequest(baseUrl, loadMethod, args);
            return result;
        }

        public string Error(string error)
        {
            List<KeyValuePair<string, string>> args = new List<KeyValuePair<string, string>>();
            args.Add(new KeyValuePair<string, string>("error", error));
            string result = SendRequest(baseUrl, errorMethod, args);
            return result;
        }

        private string SendRequest(string url, string method, List<KeyValuePair<string, string>> args)
        {
            string conStr = string.Join("&", args.Select(e => e.Key + "=" + e.Value.Replace(' ', '_')));
            string result = "error";
            try
            {
                using (WebClient wc = new WebClient())
                {
                    result = wc.DownloadString(url + method + (args.Count > 0 ? "?" : "") + conStr);
                }
            }catch(Exception ex){
                result = "error: SendRequest : "+ex.Message;
            }
            return result;
        }
    }
}
