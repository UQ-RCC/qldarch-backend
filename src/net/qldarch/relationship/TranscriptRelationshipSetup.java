package net.qldarch.relationship;

import java.sql.Timestamp;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;

import net.qldarch.db.Db;
import net.qldarch.db.Rsc;
import net.qldarch.db.Sql;
import net.qldarch.interview.Interview;
import net.qldarch.interview.Utterance;
import net.qldarch.util.M;

public class TranscriptRelationshipSetup {

  @Inject
  private Db db;

  private TranscriptRelationship createTR(Map<String, Object> m) {
    final TranscriptRelationship r = new TranscriptRelationship();
    r.setId(M.getLongNotNull(m, "relationshipId"));
    r.setRelationship(M.getStringNotNull(m, "relationship"));
    r.setNote(M.getString(m, "note"));
    r.setFrom(M.getInt(m, "fromyear"));
    r.setUntil(M.getInt(m, "untilyear"));
    r.setSubject(M.getLongNotNull(m, "subject"));
    r.setSubjectlabel(M.getStringNotNull(m, "subjectlabel"));
    r.setSubjecttype(M.getStringNotNull(m, "subjecttype"));
    r.setSubjectarchitect(M.getBoolean(m, "subjectarchitect"));
    r.setObject(M.getLongNotNull(m, "object"));
    r.setObjectlabel(M.getStringNotNull(m, "objectlabel"));
    r.setObjecttype(M.getStringNotNull(m, "objecttype"));
    r.setObjectarchitect(M.getBoolean(m, "objectarchitect"));
    r.setUtterance(M.getLongNotNull(m, "utterance"));
    r.setCreated(M.getTimestampNotNull(m, "created"));
    return r;
  }

  private Stream<TranscriptRelationship> relationships(Long interviewId) {
    try {
      return db.executeQuery(new Sql("net/qldarch/relationship/TranscriptRelationship.sql").prepare(),
          M.of("interview", interviewId), Rsc::fetchAll).stream().map(this::createTR);
    } catch(Exception e) {
      throw new RuntimeException(
          "failed to retrieve interview relationships for interview with id "+interviewId, e);
    }
  }

  public void setup(Interview interview) {
    if(interview.getTranscript() != null && interview.getTranscript().size() > 0) {
      Map<Long, Utterance> uMap = interview.getTranscript().stream().collect(
          Collectors.toMap(Utterance::getId, u->u));
      relationships(interview.getId()).forEach(ir -> {
        Utterance u = uMap.get(ir.getUtterance());
        if(u!=null) {
          if(u.getRelationships()==null) {
            u.setRelationships(new TreeSet<>());
          }
          u.getRelationships().add(ir);
        }
      });
    }
  }

}
