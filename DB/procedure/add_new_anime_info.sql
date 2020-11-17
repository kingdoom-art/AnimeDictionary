CREATE OR REPLACE PROCEDURE public.add_new_anime_info(
	"info_anime$j" json)
LANGUAGE 'plpgsql'
AS $BODY$
declare
rec$j 			json;
number_anime$i	integer:=0;
begin
	for rec$j in select * from json_array_elements(info_anime$j)
	loop
		insert into anime_info_table(id_inf, link_anime, link_image, info_anime, page, anime_number)
		values(nextval('seq_anime_info_table'), rec$j->>'link', rec$j->>'image', rec$j->>'info', rec$j->>'page', number_anime$i);
		number_anime$i := number_anime$i+1;
	end loop;
end;
$BODY$;