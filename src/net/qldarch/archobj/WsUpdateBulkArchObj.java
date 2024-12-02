package net.qldarch.archobj;

import java.io.InputStream;
import java.io.File;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Map;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
//import java.util.Date;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.Response;
import javax.mail.internet.ContentDisposition;

import org.apache.commons.lang3.StringUtils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartInput;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;


import net.qldarch.gson.JsonSkipField;
import net.qldarch.gson.serialize.CollectionRemoveNullsSerializer;
import net.qldarch.gson.serialize.JsonSerializer;
import net.qldarch.hibernate.HS;
import net.qldarch.interview.InterviewUtteranceSerializer;
import net.qldarch.interview.Utterance;
import net.qldarch.jaxrs.ContentType;
import net.qldarch.media.Media;
import net.qldarch.relationship.Relationship;
import net.qldarch.relationship.RelationshipSource;
import net.qldarch.relationship.RelationshipType;
import net.qldarch.search.update.UpdateArchObjJob;
import net.qldarch.search.update.DeleteDocumentJob;
import net.qldarch.search.update.SearchIndexWriter;
import net.qldarch.security.SignedIn;
import net.qldarch.security.UpdateEntity;
import net.qldarch.security.User;
import net.qldarch.util.ContentDispositionSupport;
import net.qldarch.util.M;
import net.qldarch.util.ObjUtils;
import net.qldarch.util.UpdateUtils;
import org.apache.poi.ss.usermodel.*;

import net.qldarch.archobj.ProjectObj;
import net.qldarch.archobj.ProjectObj.Associate;
import net.qldarch.db.Db;
import net.qldarch.db.Rsc;
import net.qldarch.db.Sql;
import java.util.HashMap;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;




@Slf4j
@Path("archobj/upload")
public class WsUpdateBulkArchObj {

    @Inject
    private HS hs;

    @Inject
    private Db db;
  
    @Inject @Nullable
    private User user;
  
    @Inject
    private SearchIndexWriter searchindexwriter;

    private String getParam(InputPart part) {
        try {
          return part.getBodyAsString();
        } catch(Exception e) {
          throw new RuntimeException("failed to read body as String", e);
        }
      }
    
    @POST
    //@Consumes("multipart/form-data")
    @Consumes("application/x-www-form-urlencoded")
    @Produces(ContentType.JSON)
    @SignedIn
   
    @UpdateEntity(entityClass=ArchObj.class)
    @JsonSkipField(type=Media.class, field="depicts")
    @JsonSerializer(type=Utterance.class, serializer=InterviewUtteranceSerializer.class)
    @JsonSerializer(path="$.precededby", serializer=SimpleArchObjSerializer.class)
    @JsonSerializer(path="$.succeededby", serializer=SimpleArchObjSerializer.class)
    @JsonSerializer(path="$.interviews", serializer=CollectionRemoveNullsSerializer.class)
    @JsonSerializer(path="$.interviews.*", serializer=IdArchObjSerializer.class)
    @JsonSerializer(path="$.interviewer.*", serializer=SimpleArchObjSerializer.class)
    @JsonSerializer(path="$.interviewee.*", serializer=SimpleArchObjSerializer.class)
   // Response create(  MultipartInput input)
    public Response create( MultivaluedMap<String, Object> data) {
        
        if((user != null) && user.isAdmin()) {
            final Map<String, Object> m = UpdateUtils.asMap(data);
            final ArchObjType type = ArchObjType.of(ObjUtils.asString(m.get("type")));
            if(type == null) {
              return Response.status(400).entity(M.of("msg", "Missing or unknown type")).build();
            }
            ArrayList<ArchObj> objs = new ArrayList<ArchObj>();
            //final Object[] projects = (Object[]) m.get("projects");
            System.out.println("class of projects ");
            System.out.println(m.get("projects").getClass().getName());
            ArrayList<String> projects = (ArrayList<String>) m.get("projects");
            System.out.println(projects);
            
            
            try{

                if (projects != null) {
                    for (String project : projects) {
                        System.out.println("class of project objects ");
                        System.out.println(project.getClass().getName());
                        System.out.println(project);
                        ObjectMapper objectMapper = new ObjectMapper();
                        ProjectObj projectObj =  objectMapper.readValue(project, ProjectObj.class);
                        System.out.print("projectObj");
                        System.out.print(projectObj);
                       /*  Map<String, Object> projectObj =null;
                            if (project instanceof Map ) {
                                projectObj = (Map<String, Object>) project;
                            } else if (project instanceof String ) {
                                projectObj = objectMapper.convertValue((String) project, new TypeReference<Map<String, Object>>() {});
                            } */

                        MultivaluedMap<String, Object> params = new MultivaluedHashMap<>();
                        ArrayList<Integer> relationshipsIDs = new ArrayList<Integer>();
                            //Map<String, Object> projectObj = (Map<String, Object>) project;
                            /* if (project instanceof Map) {
                                Map<String, Object> projectObj = (Map<String, Object>) project;
                            } */
                            //Map<String, Object> projectObj = objectMapper.readValue((String) project, Map.class);
                            if (projectObj.getTypologies()!=null)
                                params.add("typologies",projectObj.getTypologies());
                            if (projectObj.getLabel()!=null)
                                params.add("label",projectObj.getLabel());
                            if (projectObj.getLocation()!=null)
                                params.add("location",projectObj.getLocation());
                            if (projectObj.getLatitude() != 0.0){
                                double latitude = (double) projectObj.getLatitude();
                                params.add("latitude",new Float(latitude));
                            }
                            if (projectObj.getLongitude()!= 0.0){
                                double longitude = (double) projectObj.getLongitude();
                                params.add("longitude",new Float(longitude));
                            }
                            if (projectObj.getAustralian()){
                                boolean australian = (boolean) projectObj.getAustralian();
                                params.add("australian",australian);
                            }
                            if (projectObj.getDemolished()){
                                boolean demolished = (boolean) projectObj.getDemolished();
                                params.add("demolished",demolished);
                            }
                            if (projectObj.getCompletion()!=null)
                                params.add("completion",projectObj.getCompletion());

                            if (projectObj.getCompletionpd()!=null) {
                                params.add("completionpd",projectObj.getCompletionpd());
                            }
                            if (projectObj.getSummary()!=null)
                                params.add("summary",projectObj.getSummary());
                            if (projectObj.getAssociateArchitect() !=null) {
                                Associate associateArch = projectObj.getAssociateArchitect();
                                relationshipsIDs.add(associateArch.getId());
                                
                            }
                            if (projectObj.getAssociateFirm() !=null) {
                                Associate associateFirm = projectObj.getAssociateFirm();
                                relationshipsIDs.add(associateFirm.getId());
    
                            }
                                
                            try {
    
                                final Map<String, Object> mapobj = UpdateUtils.asMap(params);
                                ArchObj archobject = type.getImplementingClass().newInstance();                        
                                archobject.setType(type);
                                archobject.copyFrom(mapobj);
                                archobject.setOwner(user.getId());
                                archobject.setCreated(new Date(Instant.now().toEpochMilli()));
                                archobject.setPubts(new Timestamp(Instant.now().toEpochMilli()));
                                hs.save(archobject);
                                archobject.postCreate(mapobj);
                                VersionUtils.createNewVersion(hs, user, archobject, "initial version");
                                if (archobject.isPublished()) {
                                    try {
                                    new UpdateArchObjJob(archobject).run(searchindexwriter.getWriter());
                                    searchindexwriter.getWriter().commit();
                                    } catch(Exception e) {
                                    throw new RuntimeException("update search index failed", e);
                                    }
                                }
        
                                Long id = archobject.getId();
                                System.out.println("relationshipsIDs");
                                System.out.println(relationshipsIDs);
                                if(relationshipsIDs.size() > 0) {
                                    for (Integer ID : relationshipsIDs){
                                        Relationship relationshipobj = new Relationship();
                                        final RelationshipType Rtype = RelationshipType.valueOf(ObjUtils.asString("WorkedOn"));
                                        final RelationshipSource Rsource = RelationshipSource.valueOf(ObjUtils.asString("structure"));
                                        relationshipobj.setSubject(ID.longValue());
                                        System.out.print("relationshipobj");
                                        relationshipobj.setType(Rtype);
                                        relationshipobj.setSource(Rsource);
                                        relationshipobj.setObject(id);
                                        relationshipobj.setOwner(user.getId());
                                        relationshipobj.setCreated(new Timestamp(Instant.now().toEpochMilli()));
                                        System.out.print(relationshipobj.getSubject());
                                        try{
                                            hs.save(relationshipobj);
    
                                        } catch(Exception e) {
                                            throw new RuntimeException("failed to create relationship", e);
                                        }
                                        
                                    }
    
                                }
                                objs.add(archobject);
    
    
                            } catch(Exception e) {
                                throw new RuntimeException("failed to create archive object", e);
                            }
    
                    }
                }

                return Response.ok().entity(objs).build();
                
            }
            catch(Exception e) {
                throw new RuntimeException("failed to create project objects", e);
            }
        }
        else {
            return Response.status(403).entity(M.of("msg", "Unauthorised user")).build();
        }
    }
       
        

    
}
