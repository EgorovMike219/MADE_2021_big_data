SELECT tag_lastfm as tag, SUM(scrobbles_lastfm) as sum_scrobbles
FROM hue__tmp_artists LATERAL VIEW explode(split(tags_lastfm, '; ')) tafTable AS tag_lastfm
GROUP BY tag_lastfm
ORDER BY sum_scrobbles DESC
limit 1;