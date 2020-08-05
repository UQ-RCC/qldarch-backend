with images as (
  select distinct on (depicts) id, depicts from media
  where depicts is not null and deleted is null and
  type in ('Photograph', 'Portrait', 'Image')
  order by depicts, preferred desc nulls last, media.id
)
select
  archobj.id as interviewee,
  archobj.label,
  person.practicedinqueensland,
  person.architect,
  person.preflabel,
  images.id as media,
  array_agg(interview) as interviews
from
  archobj
  join interviewee on archobj.id = interviewee.interviewee
  join person on archobj.id = person.id
  join archobj i on interview = i.id
  left outer join images on archobj.id = images.depicts
where
  archobj.deleted is null and archobj.pubts is not null and i.deleted is null and i.pubts is not null
group by 
  archobj.id, archobj.label, person.practicedinqueensland, person.architect, person.preflabel, images.id
order by
  archobj.label
;
