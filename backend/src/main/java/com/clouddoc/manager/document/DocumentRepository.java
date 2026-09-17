package com.clouddoc.manager.document;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DocumentRepository extends JpaRepository<Document, Long> {

    @Query("""
        select document from Document document
        where document.ownerId = :ownerId
          and (:search = '' or lower(document.title) like lower(concat('%', :search, '%'))
               or lower(document.fileName) like lower(concat('%', :search, '%')))
          and (:category = '' or document.category = :category)
        order by document.uploadedAt desc
        """)
    Page<Document> search(@Param("ownerId") String ownerId,
                          @Param("search") String search,
                          @Param("category") String category,
                          Pageable pageable);
}
