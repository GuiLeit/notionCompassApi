package com.guilhermeleite.NotionCompass.domains.page;

import com.guilhermeleite.NotionCompass.domains.workspace.Workspace;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;

@Entity
@Table(name = "pages")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Page {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id", nullable = false)
    private Workspace workspace;

    @Column(name = "notion_page_id", nullable = false, unique = true)
    private String notionPageId;

    @Column(name = "notion_parent_page_id")
    private String notionParentPageId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "icon")
    private String icon;

    @Column(name = "url", nullable = false)
    private String url;

}
