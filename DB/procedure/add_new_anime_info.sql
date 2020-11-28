CREATE OR REPLACE PROCEDURE public.add_new_anime_info(
	"info_anime$j" jsonb)
LANGUAGE 'plpgsql'
AS $BODY$
declare
debug$i 		integer:=0;
rec$j 			jsonb;
number_anime$i	integer:=0;
link$t 			text;
image$t 		text;
info$t 			text;
page$t 			text;
begin
	debug$i:=1;
	for rec$j in select * from jsonb_array_elements(info_anime$j)
	loop
		begin
			if rec$j ?& array['link','image','info','page'] then
				debug$i:=2;
				link$t:=rec$j->>'link';
				debug$i:=3;
				image$t:=rec$j->>'image';
				debug$i:=4;
				info$t:=rec$j->>'info';
				debug$i:=5;
				page$t:=rec$j->>'page';
				debug$i:=6;
				insert into anime_info_table(id_inf, link_anime, link_image, info_anime, page, anime_number)
				values(nextval('seq_anime_info_table'),link$t , image$t, info$t, page$t, number_anime$i);
				number_anime$i := number_anime$i+1;
			end if;
		exception
			when others then
				raise notice 'add_new_anime_info debug = %; message = ', debug$i, sqlerrm;
		end;
	end loop;
end;
$BODY$;