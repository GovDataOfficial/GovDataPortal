<%@ include file="init.jsp"%>

<div id="A3127">
	<div class="categoriesPane row">
		<div class="small-12">
			<ul class="flex-list">
				<c:forEach items="${categories}" var="category" varStatus="row" end="6">
          <%@include file="box.jsp" %>
				</c:forEach>
			</ul>
			<ul class="flex-list">
			  <li class="category-box-spacer"></li>
				<c:forEach items="${categories}" var="category" varStatus="row" begin="7">
          <%@include file="box.jsp" %>
				</c:forEach>
				<li class="category-box-spacer"></li>
			</ul>
		</div>
	</div>
</div>