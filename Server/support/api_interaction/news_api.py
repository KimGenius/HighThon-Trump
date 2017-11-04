from datetime import datetime
import json
import re

from bs4 import BeautifulSoup as Soup
from requests import get

from db.models.news import NewsModel

_HEADERS = {
    'X-Naver-Client-Id': 'ixbtTAgetTMK_pNRfvh9',
    'X-Naver-Client-Secret': 'PYVMBVVwta'
}
# Don't Hard Coding

_API_BASE = 'https://openapi.naver.com/v1/search/news.json?query=트럼프&display=100'

_WEEKDAYS = {
    'Mon': 0,
    'Tue': 1,
    'Wed': 2,
    'Thu': 3,
    'Fri': 4,
    'Sat': 5,
    'Sun': 6
}

_MONTHS = {
    'Jan': 1,
    'Feb': 2,
    'Mar': 3,
    'Apr': 4,
    'May': 5,
    'Jun': 6,
    'Jul': 7,
    'Aug': 8,
    'Sep': 9,
    'Oct': 10,
    'Nov': 11,
    'Dec': 12
}


def _extract_datetime(str_date):
    year = int(str_date[12:16])
    month = _MONTHS[str_date[8:11]]
    day = int(str_date[5:7])
    hour = int(str_date[17:19])
    minute = int(str_date[20:22])
    second = int(str_date[23:25])

    return datetime(year, month, day, hour, minute, second)


def parse():
    NewsModel.drop_collection()

    for start in range(100, 1001, 100):
        if start == 0:
            resp = json.loads(get(_API_BASE, headers=_HEADERS).text)
        else:
            resp = json.loads(get(_API_BASE, params={'start': start, 'display': 100}, headers=_HEADERS).text)

        for item in resp['items']:
            soup = Soup(get(item['link']).text, 'html.parser')
            article = soup.find(id='articleBodyContents')

            if article is None:
                continue

            content = str(article).split('</script>\n')[1].split('<!-- //')[0].replace('<br/>', '\n').replace('&lt;', '<').replace('&gt;', '>')
            content = re.sub('<a.+</a>', '', content)

            if '이재명 기자' in content or '조성봉 기자' in content or '이지은 기자' in content:
                continue

            title = item['title'].replace('<b>', '').replace('</b>', '').replace('&quot;', "'")
            description = item['description'].replace('<b>', '').replace('</b>', '').replace('&quot;', "'")
            link = item['link']
            pub_date = _extract_datetime(item['pubDate'])

            print('{0} done.'.format(item['title']))

            NewsModel(title=title, description=description, link=link, pub_date=pub_date).save()
