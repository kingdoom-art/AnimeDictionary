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
        static void Main(string[] args)
        {
            //заглушка для проерки клиента, когда клиента еще нет
            //если нет аргументов, то это клиент, иначе это сервер запустил процесс
            switch (args.Length)
            {
                case 0:
                    //образаемся к серверу нужно для теста функционала сервера без реализации клиента
                    Console.WriteLine(ServerComand.GetInstance().LoadPage(Console.ReadLine()));
                    break;
                case 1:
                    Console.WriteLine(Parser.GetInstance().SavePage(args[0]));
                    break;
                default:
                    Console.WriteLine(ServerComand.GetInstance().Error("unknown number of arguments"));
                    break;
            }
        }

    }
}
