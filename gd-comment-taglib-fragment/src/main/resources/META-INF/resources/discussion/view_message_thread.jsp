<%--
/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
--%>

<%@ include file="/discussion/init.jsp" %>

<%
int depth = GetterUtil.getInteger(request.getAttribute("liferay-comment:discussion:depth"));
Discussion discussion = (Discussion)request.getAttribute("liferay-comment:discussion:discussion");
DiscussionComment discussionComment = (DiscussionComment)request.getAttribute("liferay-comment:discussion:discussionComment");

int index = GetterUtil.getInteger(request.getAttribute("liferay-comment:discussion:index"));

index++;

request.setAttribute("liferay-comment:discussion:index", Integer.valueOf(index));

String randomNamespace = (String)request.getAttribute("liferay-comment:discussion:randomNamespace");

DiscussionComment rootDiscussionComment = discussion.getRootDiscussionComment();

DiscussionRequestHelper discussionRequestHelper = new DiscussionRequestHelper(request);

DiscussionPermission discussionPermission = CommentManagerUtil.getDiscussionPermission(discussionRequestHelper.getPermissionChecker());

CommentTreeDisplayContext commentTreeDisplayContext = CommentDisplayContextProviderUtil.getCommentTreeDisplayContext(request, response, discussionPermission, discussionComment);

Format dateFormatDateTime = FastDateFormatFactoryUtil.getDateTime(locale, timeZone);
%>

<c:if test="<%= commentTreeDisplayContext.isDiscussionVisible() %>">
	<article class="lfr-discussion <%= (rootDiscussionComment.getCommentId() == discussionComment.getParentCommentId()) ? "lfr-discussion-container" : "" %>">
		<div class="comment-container">
			<div class="autofit-padded-no-gutters-x autofit-row widget-metadata">
				<%
				User messageUser = discussionComment.getUser();
				%>
				<div class="autofit-col">
					<c:choose>
						<c:when test="<%= (messageUser != null && messageUser.isActive()) %>">
							<liferay-ui:user-portrait
								cssClass="sticker-lg"
								userId="<%= discussionComment.getUserId() %>"
								userName="<%= discussionComment.getUserName() %>"
							/>
						</c:when>
						<c:otherwise>
							<liferay-ui:user-portrait
								cssClass="sticker-lg"
								userId="<%= -1 %>"
								userName="<%= LanguageUtil.get(resourceBundle, \"anonymous\") %>"
							/>
						</c:otherwise>
					</c:choose>
				</div>

				<div class="autofit-col autofit-col-expand">
					<div class="autofit-row">
						<div class="autofit-col autofit-col-expand">
							<div class="text-truncate">
								<liferay-util:whitespace-remover>
									<c:choose>
										<c:when test="<%= (messageUser != null && messageUser.isActive()) %>">
											<aui:a cssClass="username" href="<%= messageUser.getDisplayURL(themeDisplay) %>">
												<c:choose>
													<c:when test="<%= discussionComment.getUserId() == user.getUserId() %>">
														<%= HtmlUtil.escape(discussionComment.getUserName()) %> (<liferay-ui:message key="you" />)
													</c:when>
													<c:otherwise>
														<%= HtmlUtil.escape(discussionComment.getUserName()) %>
													</c:otherwise>
												</c:choose>
											</aui:a>
										</c:when>
										<c:otherwise>
											<aui:a cssClass="username" href="">
												<%= LanguageUtil.get(resourceBundle, "anonymous") %>
											</aui:a>
										</c:otherwise>
									</c:choose>
								</liferay-util:whitespace-remover>

								<%
								Date createDate = discussionComment.getCreateDate();

								String createDateDescription = LanguageUtil.getTimeDescription(request, System.currentTimeMillis() - createDate.getTime(), true);
								%>

								<c:if test="<%= discussionComment.getParentCommentId() != rootDiscussionComment.getCommentId() %>">

									<%
									DiscussionComment parentDiscussionComment = discussionComment.getParentComment();

									User parentMessageUser = parentDiscussionComment.getUser();
									boolean parentMessageUserActive = (parentMessageUser != null && parentMessageUser.isActive());
									String parentMessageUserName = "";
									if (parentMessageUserActive) {
										parentMessageUserName = parentDiscussionComment.getUserName();
									} else {
										parentMessageUserName = LanguageUtil.get(resourceBundle, "anonymous");
									}
									%>

									<liferay-util:buffer
										var="parentCommentUserBuffer"
									>
										<div class="autofit-padded-no-gutters-x autofit-row">
											<div class="autofit-col">
												<c:choose>
													<c:when test="<%= parentMessageUserActive == true %>">
														<liferay-ui:user-portrait
															cssClass="sticker-lg"
															user="<%= parentMessageUser %>"
														/>
													</c:when>
													<c:otherwise>
														<liferay-ui:user-portrait
															cssClass="sticker-lg"
															userId="<%= -1 %>"
															userName="<%= parentMessageUserName %>"
														/>
													</c:otherwise>
												</c:choose>
											</div>

											<div class="autofit-col autofit-col-expand">
												<div class="username">
													<%= HtmlUtil.escape(parentMessageUserName) %>
												</div>

												<%
												Date parentDiscussionCreateDate = parentDiscussionComment.getCreateDate();
												%>

												<div class="text-secondary">
													<liferay-ui:message arguments="<%= LanguageUtil.getTimeDescription(request, System.currentTimeMillis() - parentDiscussionCreateDate.getTime(), true) %>" key="x-ago" translateArguments="<%= false %>" />
												</div>
											</div>
										</div>
									</liferay-util:buffer>

									<%
									Map<String, String> dataInReply = HashMapBuilder.put(
										"inreply-content", parentDiscussionComment.getBody()
									).put(
										"inreply-title", parentCommentUserBuffer
									).build();
									%>

									<clay:link
										ariaLabel='<%= LanguageUtil.format(request, "in-reply-to-x", HtmlUtil.escape(parentMessageUserName), false) %>'
										data="<%= dataInReply %>"
										elementClasses="lfr-discussion-parent-link"
										href='<%= "#" + randomNamespace + "message_" + parentDiscussionComment.getCommentId() %>'
										icon="redo"
										label="<%= HtmlUtil.escape(parentMessageUserName) %>"
									/>
								</c:if>
							</div>

							<div class="text-secondary">
								<span title="<%= dateFormatDateTime.format(createDate) %>"><liferay-ui:message arguments="<%= createDateDescription %>" key="x-ago" translateArguments="<%= false %>" /></span>

								<%
								Date modifiedDate = discussionComment.getModifiedDate();
								%>

								<c:if test="<%= createDate.before(modifiedDate) %>">
									-
									<strong title="<%= dateFormatDateTime.format(modifiedDate) %>">
										<liferay-ui:message key="edited" />
									</strong>
								</c:if>

								<c:if test="<%= commentTreeDisplayContext.isWorkflowStatusVisible() %>">

									<%
									WorkflowableComment workflowableComment = (WorkflowableComment)discussionComment;
									%>

									<aui:model-context bean="<%= workflowableComment %>" model="<%= workflowableComment.getModelClass() %>" />

									<aui:workflow-status model="<%= CommentConstants.getDiscussionClass() %>" showIcon="<%= false %>" showLabel="<%= false %>" status="<%= workflowableComment.getStatus() %>" />
								</c:if>
							</div>
						</div>
					</div>
				</div>

				<c:if test="<%= commentTreeDisplayContext.isActionControlsVisible() && (index > 0) %>">
					<div class="autofit-col">
						<liferay-ui:icon-menu
							cssClass="actions-menu"
							direction="left-side"
							icon="<%= StringPool.BLANK %>"
							markupView="lexicon"
							message="actions"
							showWhenSingleIcon="<%= true %>"
						>
							<c:if test="<%= commentTreeDisplayContext.isEditActionControlVisible() %>">
								<liferay-ui:icon
									message="edit"
									url='<%= "javascript:" + randomNamespace + "showEditReplyEditor(" + index + ");" %>'
								/>
							</c:if>

							<c:if test="<%= commentTreeDisplayContext.isDeleteActionControlVisible() %>">
								<liferay-ui:icon-delete
									label="<%= true %>"
									url='<%= "javascript:" + randomNamespace + "deleteMessage(" + index + ");" %>'
								/>
							</c:if>
						</liferay-ui:icon-menu>
					</div>
				</c:if>
			</div>

			<div class="lfr-discussion-body">
				<div id="<%= randomNamespace %>messageScroll<%= discussionComment.getCommentId() %>">
					<a id="<%= randomNamespace %>message_<%= discussionComment.getCommentId() %>" name="<%= randomNamespace %>message_<%= discussionComment.getCommentId() %>"></a>

					<aui:input name='<%= "commentId" + index %>' type="hidden" value="<%= discussionComment.getCommentId() %>" />
					<aui:input name='<%= "parentCommentId" + index %>' type="hidden" value="<%= discussionComment.getCommentId() %>" />
				</div>

				<div class="lfr-discussion-message">
					<div class="lfr-discussion-message-body" id="<%= namespace + "discussionMessage" + index %>">
						<%= discussionComment.getTranslatedBody(themeDisplay.getPathThemeImages()) %>
					</div>

					<c:if test="<%= commentTreeDisplayContext.isEditControlsVisible() %>">
						<div class="lfr-discussion-form lfr-discussion-form-edit" id="<%= namespace %>editForm<%= index %>" style="display: none;">
							<div class="editor-wrapper"></div>

							<aui:button-row>
								<aui:button cssClass="btn-comment btn-primary btn-sm" name='<%= "editReplyButton" + index %>' onClick='<%= randomNamespace + "updateMessage(" + index + ");" %>' value="<%= commentTreeDisplayContext.getPublishButtonLabel(locale) %>" />

								<%
								String taglibCancel = randomNamespace + "showEl('" + namespace + "discussionMessage" + index + "');" + randomNamespace + "hideEditor('" + randomNamespace + "editReplyBody" + index + "', '" + namespace + "editForm" + index + "');";
								%>

								<aui:button cssClass="btn-comment btn-primary btn-sm" onClick="<%= taglibCancel %>" type="cancel" />
							</aui:button-row>

							<aui:script>
								window['<%= namespace + index %>EditOnChange'] = function (html) {
									Liferay.Util.toggleDisabled(
										'#<%= namespace %>editReplyButton<%= index %>',
										html.trim() === ''
									);
								};
							</aui:script>
						</div>
					</c:if>
				</div>

				<div class="autofit-row lfr-discussion-controls">
					<c:if test="<%= commentTreeDisplayContext.isActionControlsVisible() && commentTreeDisplayContext.isReplyActionControlVisible() %>">
						<div class="autofit-col">
							<c:if test="<%= !discussion.isMaxCommentsLimitExceeded() %>">
								<c:choose>
									<c:when test="<%= commentTreeDisplayContext.isReplyButtonVisible() %>">
										<button class="btn btn-outline-borderless btn-outline-secondary btn-sm" onclick="<%= randomNamespace + "showPostReplyEditor(" + index + ");" %>" type="button">
											<liferay-ui:message key="reply" />
										</button>
									</c:when>
									<c:otherwise>
										<a class="btn btn-outline-borderless btn-outline-secondary btn-sm" href="<%= themeDisplay.getURLSignIn() %>">
											<liferay-ui:message key="please-sign-in-to-reply" />
										</a>
									</c:otherwise>
								</c:choose>
							</c:if>
						</div>
					</c:if>

					<c:if test="<%= commentTreeDisplayContext.isRatingsVisible() %>">
						<div class="autofit-col">
							<liferay-ratings:ratings
								className="<%= CommentConstants.getDiscussionClassName() %>"
								classPK="<%= discussionComment.getCommentId() %>"
								inTrash="<%= false %>"
								ratingsEntry="<%= discussionComment.getRatingsEntry() %>"
								ratingsStats="<%= discussionComment.getRatingsStats() %>"
							/>
						</div>
					</c:if>
				</div>
			</div>
		</div>

		<div class="lfr-discussion lfr-discussion-form-reply" id="<%= namespace + "postReplyForm" + index %>" style="display: none;">
			<div class="lfr-discussion-reply-container">
				<div class="autofit-padded-no-gutters autofit-row">
					<div class="autofit-col lfr-discussion-details">
						<liferay-ui:user-portrait
							cssClass="sticker-lg"
							user="<%= user %>"
						/>
					</div>

					<div class="autofit-col autofit-col-expand">
						<div class="editor-wrapper"></div>

						<aui:button-row>
							<aui:button cssClass="btn-comment btn-primary btn-sm" disabled="<%= true %>" id='<%= "postReplyButton" + index %>' onClick='<%= randomNamespace + "postReply(" + index + ");" %>' value='<%= themeDisplay.isSignedIn() ? "reply" : "reply-as" %>' />

							<%
							String taglibCancel = randomNamespace + "hideEditor('" + randomNamespace + "postReplyBody" + index + "', '" + namespace + "postReplyForm" + index + "')";
							%>

							<aui:button cssClass="btn-comment btn-sm" onClick="<%= taglibCancel %>" type="cancel" />
						</aui:button-row>

						<aui:script>
							window['<%= namespace + index %>ReplyOnChange'] = function (html) {
								Liferay.Util.toggleDisabled(
									'#<%= namespace %>postReplyButton<%= index %>',
									html.trim() === ''
								);
							};
						</aui:script>
					</div>
				</div>
			</div>
		</div>

		<%
		for (DiscussionComment curDiscussionComment : discussionComment.getDescendantComments()) {
			request.setAttribute("liferay-comment:discussion:depth", depth + 1);
			request.setAttribute("liferay-comment:discussion:discussionComment", curDiscussionComment);
		%>

			<liferay-util:include page="/discussion/view_message_thread.jsp" servletContext="<%= application %>" />

		<%
		}
		%>

	</article>
</c:if>