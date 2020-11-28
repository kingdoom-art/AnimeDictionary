CREATE OR REPLACE FUNCTION public.check_page(
	"page$t" text,
	"anime_number$i" integer)
    RETURNS boolean
    LANGUAGE 'plpgsql'

    COST 100
    VOLATILE 
    
AS $BODY$
declare
result$b	boolean := false;
col$bi		bigint;
begin
	select count(1)
	into col$bi
	where exists(select 1
				from anime_info_table ait
				where ait.page = page$t
					and ait.anime_number = anime_number$i
				);
	if col$bi > 0 then
		result$b:=true;
	end if;
return result$b;
end;
$BODY$;