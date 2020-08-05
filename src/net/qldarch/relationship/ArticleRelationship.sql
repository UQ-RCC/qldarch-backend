select
  r.id as relationshipId,
  r.type as relationship,
  r.fromyear,
  r.untilyear,
  r.subject,
  s.label as subjectlabel,
  s.type as subjecttype,
  sp.architect as subjectarchitect,
  r.object,
  o.label as objectlabel,
  o.type as objecttype,
  op.architect as objectarchitect,
  r.note,
  ar.article,
  ar.page,
  r.created
from 
  relationship r
  join archobj s on r.subject = s.id
  left join person sp on s.id = sp.id
  join archobj o on r.object = o.id
  left join person op on o.id = op.id
  join articlerelationship ar on r.id = ar.id
where 
  ar.article = :article
;
