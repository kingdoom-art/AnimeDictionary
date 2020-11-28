CREATE OR REPLACE FUNCTION public.get_anime_info(
	"page$t" text,
	"anime_number$i" integer)
    RETURNS json
    LANGUAGE 'plpgsql'

    COST 100
    VOLATILE 
    
AS $BODY$
declare
result$j 		json;
tmp_result$b	boolean;
_page$t			text:=page$t;
_anime_number$i	integer:=anime_number$i;
begin
	tmp_result$b := check_page(_page$t, _anime_number$i);
	if not tmp_result$b then
		begin
			if _anime_number$i >= 0 then
				select distinct ait.page
				into strict _page$t
				from anime_info_table ait
				where ait.page > _page$t
				order by ait.page asc
				limit 1;
			else 
				select distinct ait.page
				into strict _page$t
				from anime_info_table ait
				where ait.page < _page$t
				order by ait.page desc
				limit 1;
			end if;
		exception
			when no_data_found then
				if _anime_number$i >= 0 then
					select distinct ait.page
					into strict _page$t
					from anime_info_table ait
					order by ait.page asc
					limit 1;
				else 
					select distinct ait.page
					into strict _page$t
					from anime_info_table ait
					order by ait.page desc
					limit 1;
				end if;
		end;
		_anime_number$i:=0;
	end if;
	
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
	where ait.page = _page$t
		and ait.anime_number = _anime_number$i
	limit 1;
	
return result$j;
end;
$BODY$;