import { post } from '@/utils/request';
import { getMenuData, getPageTitle } from '@ant-design/pro-layout';
import { useModel } from '@umijs/max';
import { message } from 'antd';
import { useEffect } from 'react';
import { Helmet, HelmetProvider } from 'react-helmet-async';
import { Outlet, useIntl } from 'umi';
import styles from './UserLayout.less';

const UserLayout = (props) => {
  const {
    route = {
      routes: [],
    },
  } = props;
  const { routes = [] } = route;
  const {
    location = {
      pathname: '',
    },
  } = props;
  const { fInfo, setFInfo } = useModel('global');
  console.log(fInfo);

  const getBasicData = async () => {
    const res = await post(`/guzhe/system/basic/config`);
    if (res && res.code === 10000) {
      const object = {};
      res.data.map((item) => {
        object[item.key] = item.value;
        return item;
      });
      localStorage.setItem('config', JSON.stringify(object));
      setFInfo({
        version: object?.version,
        miitbeian: object?.miitbeian,
        logo: object?.logo,
        name: object?.name,
      });
    } else {
      message.error(res?.msg);
    }
  };
  useEffect(() => {
    getBasicData();
  }, []);
  const { formatMessage } = useIntl();
  const { breadcrumb } = getMenuData(routes);
  // console.log(location.pathname,formatMessage,breadcrumb)
  const title = getPageTitle({
    pathname: location.pathname,
    formatMessage,
    breadcrumb,
    ...props,
  });
  return (
    <HelmetProvider>
      <Helmet>
        <title>{title}</title>
        <meta name="description" content={title} />
      </Helmet>
      <div className={styles.container}>
        <img
          alt=""
          className={styles.xx1}
          src={require('../assets/images/1.png')}
        />

        <img
          alt=""
          className={styles.xx3}
          src={require('../assets/images/3.png')}
        />

        <img
          alt=""
          className={styles.xx33}
          src={require('../assets/images/33.png')}
        />

        <img
          alt=""
          className={styles.xx4}
          src={require('../assets/images/4.png')}
        />

        {/* <div className={styles.lang}>
          <SelectLang />
        </div> */}
        <img
          style={{ cursor: 'pointer' }}
          alt=""
          className={styles.zcLogo}
          src={require('../assets/images/disc.png')}
          width="240"
          onClick={() => {
            window.open('https://www.bluewise.cc');
          }}
        />
        <div className={styles.content}>
          <div className={styles.top} />
          <div style={{ display: 'flex', alignItems: 'center' }}>
            <Outlet />{' '}
          </div>
        </div>
        <div className={styles.footer}>
          {fInfo.version}&nbsp;
          <a
            style={{ color: '#fff', textDecoration: 'none' }}
            href="https://beian.miit.gov.cn/"
          >
            {fInfo.miitbeian}
          </a>
          <div style={{ textAlign: 'center' }}>
            技术支持：
            <a
              href="https://www.bluewise.cc"
              target="_blank"
              rel="noreferrer"
              style={{ color: '#fff', textDecoration: 'none' }}
            >
              东莞市蓝睿网络科技有限公司
            </a>
          </div>
        </div>
        {/* <DefaultFooter /> */}
      </div>
    </HelmetProvider>
  );
};

export default UserLayout;
