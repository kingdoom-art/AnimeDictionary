using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Server
{
    [Serializable]
    class InfoAnime
    {
        public string image;
        public string link;
        public string info;
        public string page;

        public InfoAnime(string link, string image, string info, string page)
        {
            this.image = image;
            this.link = link;
            this.info = info;
            this.page = page;
        }
    }
}
