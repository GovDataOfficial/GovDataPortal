<div id="main-content" role="main">
  <div class="layout-20-80">
    <div class="portlet-layout">
      <div class="portlet-column portlet-column-only" id="column-1">
        $processor.processColumn("column-1", "portlet-column-content portlet-column-content-only")
      </div>
    </div>
    <div class="portlet-layout row">
      <div class="portlet-column portlet-column-first column small-12 large-3" id="column-2">
        $processor.processColumn("column-2", "portlet-column-content portlet-column-content-first")
      </div>
      <div class="portlet-column portlet-column-last column small-12 large-9" id="column-3">
        $processor.processColumn("column-3", "portlet-column-content portlet-column-content-last")
      </div>
    </div>
  </div>
</div>
