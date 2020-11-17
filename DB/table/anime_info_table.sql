CREATE TABLE public.anime_info_table
(
    id_inf integer NOT NULL,
    link_anime text COLLATE pg_catalog."default",
    link_image text COLLATE pg_catalog."default",
    info_anime text COLLATE pg_catalog."default",
    anime_number integer,
    page text COLLATE pg_catalog."default",
    CONSTRAINT "AnimeInfoTable_pkey" PRIMARY KEY (id_inf)
)

TABLESPACE pg_default;

ALTER TABLE public.anime_info_table
    OWNER to postgres;