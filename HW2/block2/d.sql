SELECT country_mb as country, COUNT(1) as n_artist
FROM hue__tmp_artists as artists
WHERE scrobbles_lastfm > 1000000
GROUP BY country_mb
ORDER BY n_artist DESC
LIMIT 20