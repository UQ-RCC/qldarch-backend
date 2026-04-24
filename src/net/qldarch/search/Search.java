package net.qldarch.search;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;

import net.qldarch.guice.Bind;
import lombok.extern.slf4j.Slf4j;

import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.WildcardQuery;

@Slf4j
@Bind
public class Search {

  public SearchResult search(String q, int page, int pagecount, Directory d)  {
    try(DirectoryReader ireader = DirectoryReader.open(d)) {

      if(ireader.numDocs() > 0) {
        Document sample = ireader.document(0);
        log.debug("Sample document fields: {}", 
        sample.getFields().stream()
          .map(f -> f.name() + "=" + f.stringValue())
          .collect(Collectors.joining(", ")));
      }
      log.debug("Total docs in index: {}", ireader.numDocs());


      IndexSearcher isearcher = new IndexSearcher(ireader);
      // TODO somehow add the ids to query to improve performance
      // (e.g. in this case we would get around fetching every document and filter by id afterwards,
      // also the topdocs.totalHits would be correct and also the search function can be 
      // passed from+pagecount as a limit instead of Integer.MAX_VALUE) 
      TopDocs topdocs = isearcher.search(buildQuery(q), Integer.MAX_VALUE);
      List<Document> documents = Arrays.stream(topdocs.scoreDocs).map(sdoc -> {
        try {
          return isearcher.doc(sdoc.doc);
        } catch(IOException e) {
          throw new RuntimeException(e);
        }
      }).filter(Objects::nonNull).collect(Collectors.toList());
      final int totalHits = documents.size();
      return new SearchResult(totalHits, page, pagecount, documents.stream().skip(
          page * pagecount).limit(pagecount).collect(Collectors.toList()));
    } catch(ParseException e) {
    // Query was malformed — log it so you can see what the frontend sent
        log.warn("Failed to parse search query: '{}', error: {}", q, e.getMessage());
        return new SearchResult(0, page, pagecount, Collections.emptyList());
      } catch(Exception e) {
        log.warn("Unexpected search exception for query: '{}'", q, e);
        return new SearchResult(0, page, pagecount, Collections.emptyList());
      }
  }

  /* private Query buildQuery(final String q) throws ParseException {
      // Clean and lowercase the user's search term only
      String term = q.toLowerCase().trim();
      
      // Build the name wildcard query against the "all" field
      Query nameQuery = new WildcardQuery(new Term("all", term + "*"));

      // Build type filter: person OR firm OR structure
      BooleanQuery.Builder typeFilter = new BooleanQuery.Builder();
      typeFilter.add(new TermQuery(new Term("type", "person")), BooleanClause.Occur.SHOULD);
      typeFilter.add(new TermQuery(new Term("type", "firm")), BooleanClause.Occur.SHOULD);
      typeFilter.add(new TermQuery(new Term("type", "structure")), BooleanClause.Occur.SHOULD);
      typeFilter.setMinimumNumberShouldMatch(1);

      // Build category filter
      Query categoryFilter = new TermQuery(new Term("category", "archobj"));

      // Combine: name AND (type filter) AND category
      BooleanQuery.Builder finalQuery = new BooleanQuery.Builder();
      finalQuery.add(nameQuery, BooleanClause.Occur.MUST);
      finalQuery.add(typeFilter.build(), BooleanClause.Occur.MUST);
      finalQuery.add(categoryFilter, BooleanClause.Occur.MUST);

      Query query = finalQuery.build();
      log.info("Executing query: {}", query.toString());
      return query;
  } */

  private Query buildQuery(final String q) throws ParseException {
    Analyzer analyzer = new StandardAnalyzer();
    QueryParser parser = new QueryParser("all", analyzer);
    parser.setDefaultOperator(QueryParser.Operator.AND);
    parser.setAllowLeadingWildcard(true);
    Query query = parser.parse(q);
    log.debug("Parsed query: {}", query.toString());
    return query;
  } 

}
