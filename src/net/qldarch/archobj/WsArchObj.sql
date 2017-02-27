with images as (
  select distinct on (depicts) id, depicts from media
  where depicts is not null and deleted is null and
  type in ('Photograph', 'Portrait', 'Image', 'LineDrawing')
  order by depicts, preferred desc nulls last, media.id
)
select
  r.id as relationshipId,
  r.type as relationship,
  r.note,
  r.fromyear,
  r.untilyear,
  r.subject,
  s.label as subjectlabel,
  s.type as subjectype,
  sp.practicedinqueensland as subjectpracticedinqueensland,
  sp.architect as subjectarchitect,
  r.object,
  o.label as objectlabel,
  o.type as objecttype,
  op.practicedinqueensland as objectpracticedinqueensland,
  op.architect as objectarchitect,
  r.source,
  ir.interview,
  ir.utterance,
  images.id as media
from
  relationship r
  join archobj s on r.subject = s.id
  left join person sp on s.id = sp.id
  join archobj o on r.object = o.id
  left join person op on o.id = op.id
  left join interviewrelationship ir on r.id = ir.id
  left join images on r.object = images.depicts
where
  r.subject = :id and
  o.deleted is null
union
select
  r.id as relationshipId,
  r.type as relationship,
  r.note,
  r.fromyear,
  r.untilyear,
  r.subject,
  s.label as subjectlabel,
  s.type as subjectype,
  sp.practicedinqueensland as subjectpracticedinqueensland,
  sp.architect as subjectarchitect,
  r.object,
  o.label as objectlabel,
  o.type as objecttype,
  op.practicedinqueensland as objectpracticedinqueensland,
  op.architect as objectarchitect,
  r.source,
  ir.interview,
  ir.utterance,
  images.id as media
from
  relationship r
  join archobj s on r.subject = s.id
  left join person sp on s.id = sp.id
  join archobj o on r.object = o.id
  left join person op on o.id = op.id
  left join interviewrelationship ir on r.id = ir.id
  left join images on r.subject = images.depicts
where
  r.object = :id and
  s.deleted is null
;