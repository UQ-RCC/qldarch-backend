https://qldarch.dev.localhost/ws/interviewrelationship

curl -X PUT -d 'note=test+note&interview=2561&utterance=15504&source=interview&subject=132&type=InfluencedBy&object=1287' -H 'content-type: application/x-www-form-urlencoded' -H 'cookie: sessionid=39f27ce54e9e45626d574a5c778641d7ff7b3a5d' https://qldarch.dev.localhost/ws/interviewrelationship


curl -X PUT -d 'note=test&article=2794&page=1&source=article&subject=1&type=InfluencedBy&object=1' -H 'content-type: application/x-www-form-urlencoded' -H 'cookie: sessionid=39f27ce54e9e45626d574a5c778641d7ff7b3a5d' https://qldarch.dev.localhost/ws/articlerelationship


test delete index of unpublished record


curl -X POST -d 'id=2794&authors=test' -H 'content-type: application/x-www-form-urlencoded' https://qldarch.dev.localhost/ws/archobj
