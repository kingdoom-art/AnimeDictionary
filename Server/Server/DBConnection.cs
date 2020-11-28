using Newtonsoft.Json;
using Npgsql;
using System;
using System.Collections.Generic;
using System.Configuration;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Server
{
    class DBConnection
    {
        public string connect { get; } = ConfigurationManager.ConnectionStrings["toDataBase"].ConnectionString;
        public string queryPage { get; } = "call add_new_anime_info(\'{0}\');";

        public DBConnection()
        {

        }

        //сохранение в бд
        public string SaveListInfo(List<InfoAnime> mas)
        {
            //делаем джейсон
            string str = JsonConvert.SerializeObject(mas);
            List<string> args = new List<string>();
            args.Add(str);
            return QueryToDB(queryPage, args);
        }

        private string QueryToDB(string _query, List<string> args)
        {
            //если коннект к базе не удался, отправим серверу инфу об этом
            try
            {
                //коннектимся
                NpgsqlConnection conn = new NpgsqlConnection(connect);
                conn.Open();
                //процедуркой заносим все в бд
                string query = string.Format(_query, args.ToArray());

                NpgsqlCommand command = new NpgsqlCommand(query, conn);
                command.ExecuteNonQuery();
                //чилим
                conn.Close();
                return "ok";
            }
            catch (Exception ex)
            {
                ServerComand.GetInstance().Error("QueryToDB error save result "+ex.Message);
                return "error";
            }
        }
    }
}
