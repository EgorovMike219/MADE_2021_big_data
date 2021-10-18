SELECT topTag.tag as tag, topTag.sum_scrobbles as tag_scrobbles, tagArtist.artist_lastfm as artist, tagArtist.scrobbles as artist_scrobbles 
FROM (
    SELECT tag_lastfm as tag, SUM(scrobbles_lastfm) as sum_scrobbles
    FROM hue__tmp_artists LATERAL VIEW explode(split(tags_lastfm, '; ')) tafTable AS tag_lastfm
    GROUP BY tag_lastfm
    ORDER BY sum_scrobbles DESC
    limit 10
    ) topTag
LEFT JOIN (
    SELECT artists.artist_lastfm, artistTag.tag as tag, artists.scrobbles_lastfm as scrobbles
    FROM hue__tmp_artists as artists 
    INNER JOIN (
        SELECT tag_lastfm as tag, MAX(scrobbles_lastfm) as max_scrobbles 
        FROM hue__tmp_artists 
        LATERAL VIEW explode(split(tags_lastfm, '; ')) tagTable AS tag_lastfm
        GROUP BY tag_lastfm
    ) AS artistTag
    ON artists.scrobbles_lastfm = artistTag.max_scrobbles AND array_contains(split(artists.tags_lastfm, '; '), artistTag.tag)
) AS tagArtist
ON topTag.tag = tagArtist.tag;