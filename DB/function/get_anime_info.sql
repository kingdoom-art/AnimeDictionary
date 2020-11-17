CREATE OR REPLACE FUNCTION public.get_anime_info(
	"page$t" text,
	"anime_number$i" integer)
    RETURNS json
    LANGUAGE 'plpgsql'

    COST 100
    VOLATILE 
    
AS $BODY$
declare
result$j json;
begin
	select json_build_object(
			'id_inf', ait.id_inf
			,'link_anime', ait.link_anime
			,'link_image', ait.link_image
			,'info_anime', ait.info_anime
			,'anime_number', ait.anime_number
			,'page', ait.page
		)
	into result$j
	from anime_info_table ait
	where ait.page = page$t
	and ait.anime_number = anime_number$i
	limit 1;
return result$j;
end;
$BODY$;

ALTER FUNCTION public.get_anime_info(text, integer)
    OWNER TO postgres;