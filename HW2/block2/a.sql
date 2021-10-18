SELECT artist_lastfm, scrobbles_lastfm
FROM hue__tmp_artists as artists
    INNER JOIN (
    SELECT MAX(scrobbles_lastfm) as scrobbles FROM hue__tmp_artists
    ) tmp
    ON artists.scrobbles_lastfm = tmp.scrobbles;