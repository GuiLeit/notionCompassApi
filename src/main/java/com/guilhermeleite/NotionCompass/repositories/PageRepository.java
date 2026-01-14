package com.guilhermeleite.NotionCompass.repositories;

import com.guilhermeleite.NotionCompass.domains.page.Page;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PageRepository extends JpaRepository<Page, Long> {
}
