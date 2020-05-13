DO
$$
begin

if not exists (
    select 1
    from   manufacturer
    where  name = 'Kaifa'
    ) then

insert into manufacturer(
    id,
    name,
    code,
    use_prefix)
values (
    nextval('manufacturer_id_seq'),
    'Kaifa',
    'KAIF',
    false);

end if;

if not exists (
    select 1
    from   manufacturer
    where  name = 'L+G'
    ) then

insert into manufacturer(
    id,
    name,
    code,
    use_prefix)
values (
    nextval('manufacturer_id_seq'),
    'L+G',
    'LAGY',
    false);

end if;

if not exists (
    select 1
    from   manufacturer
    where  name = 'Iskraemeco'
    ) then

insert into manufacturer(
    id,
    name,
    code,
    use_prefix)
values (
    nextval('manufacturer_id_seq'),
    'Iskraemeco',
    'Iskr',
    false);

end if;

end;
$$
