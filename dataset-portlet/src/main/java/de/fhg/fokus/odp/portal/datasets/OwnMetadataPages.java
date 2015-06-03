package de.fhg.fokus.odp.portal.datasets;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fhg.fokus.odp.registry.model.Metadata;

/**
 * Implements paging for own Metadata.
 */
public class OwnMetadataPages {
	private static final Logger log = LoggerFactory
			.getLogger(OwnMetadataPages.class);
	public static final int PAGESIZECONST = 15;

	private List<String> m_currentOwnMetadata;
	private CurrentUser m_currentUser;

	private int page_size = PAGESIZECONST;
	private int currentpage;
	private int start;
	private int end;
	private int max_pages;
	private int offset;

	public OwnMetadataPages(CurrentUser currentUser) {
		m_currentUser = currentUser;
		List<String> datasets = currentUser.getUserDatasets();
		this.m_currentOwnMetadata = datasets;
		this.currentpage = 1;
		this.max_pages = 1;
		refreshPages();
	}

	public void setOffset(int i) {
		offset = i;
	}

	public int getOffset() {
		return offset;
	}

	private void refreshPages() {
		if (page_size > 0) {
			// calculate how many pages there are
			if (m_currentOwnMetadata.size() % page_size == 0) {
				max_pages = m_currentOwnMetadata.size() / page_size;
			} else {
				max_pages = (m_currentOwnMetadata.size() / page_size) + 1;
			}
		}
	}

	public List<String> getCurrentOwnMetadata() {
		return this.m_currentOwnMetadata;
	}

	public List<String> getOwnMetadataForPage() {
		return m_currentOwnMetadata.subList(start, end);
	}

	public int getPageSize() {
		return this.page_size;
	}

	public void setPageSize(int pageSize) {
		this.page_size = pageSize;
		refreshPages();
	}

	public int getPage() {
		return this.currentpage;
	}

	public List<Integer> getPages() {
		List<Integer> pages = new ArrayList<Integer>();
		int num = getMaxPages();
		for (int i = 1; i <= num; ++i) {
			pages.add(Integer.valueOf(i));
		}
		return pages;
	}

	public void setPage(int p) {
		if (p >= max_pages) {
			this.currentpage = max_pages;
		} else if (p <= 1) {
			this.currentpage = 1;
		} else {
			this.currentpage = p;
		}
		start = page_size * (currentpage - 1);
		if (start < 0) {
			start = 0;
		}
		setOffset(start);
		end = start + page_size;
		if (end > m_currentOwnMetadata.size()) {
			end = m_currentOwnMetadata.size();
		}
	}

	public int getMaxPages() {
		return this.max_pages;
	}

	public List<Metadata> getMetadatas() {
		return m_currentUser.getUserMetadatas(getOwnMetadataForPage());
	}

}
