package net.qldarch.architect;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import net.qldarch.WsBase;
import net.qldarch.archobj.ArchObj;
import net.qldarch.db.Db;
import net.qldarch.db.Rsc;
import net.qldarch.db.Sql;
import net.qldarch.jaxrs.ContentType;

@Path("/architects")
public class WsArchitects extends WsBase<ArchObj> {

}
