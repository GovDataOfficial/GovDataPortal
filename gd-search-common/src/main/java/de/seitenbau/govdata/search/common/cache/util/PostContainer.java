package de.seitenbau.govdata.search.common.cache.util;

import java.time.LocalDateTime;

import org.apache.commons.lang3.StringUtils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Store Tweet & Post information.
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostContainer
{
  private String text;

  private String id;

  private String url;

  private String name;

  private String username;

  private LocalDateTime timestamp;

  private String type;

  /**
   * Returns the state if the post is of type retweet or boosted.
   * @return true if the post is a retweet, otherwise false.
   */
  public boolean isRetweet()
  {
    return StringUtils.equals(type, SocialMediaPlatformsConsts.TWITTER_RETWEETED)
        || StringUtils.equals(type, SocialMediaPlatformsConsts.MASTODON_BOOSTED);
  }

}
