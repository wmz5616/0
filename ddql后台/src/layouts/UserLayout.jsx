import { DefaultFooter, getMenuData, getPageTitle } from '@ant-design/pro-layout';
import { Helmet, HelmetProvider } from 'react-helmet-async';
import { Link, SelectLang, useIntl, connect } from 'umi';
import React, { useEffect } from 'react';
import logo from '../assets/logo.png';
import styles from './UserLayout.less';

const UserLayout = (props) => {
  const {
    route = {
      routes: [],
    },
  } = props;
  const { routes = [] } = route;
  const {
    children,
    dispatch,
    location = {
      pathname: '',
    },
  } = props;
  useEffect(() => {
    if (props.dispatch) {
      props.dispatch({
        type: 'myModel/getSetData',
        url: `/ddql/system/basic/config`,
        method: 'POST',
        myData: (res) => {
          if (res && res.code === 10000) {
            const object = {};
            res.data.map((item) => {
              object[item.key] = item.value;
              return item;
            });
            dispatch({
              type: 'global/save',
              payload: {
                version: object?.version,
                miitbeian: object?.miitbeian,
                logo: object?.logo,
                name: object?.name,
              },
            });
          } else {
            message.error(res.msg);
          }
        },
      });
    }
  }, [dispatch]);
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
        <img alt="" className={styles.xx1} src={require('../assets/images/1.png')} />

        <img alt="" className={styles.xx3} src={require('../assets/images/3.png')} />

        <img alt="" className={styles.xx33} src={require('../assets/images/33.png')} />

        <img alt="" className={styles.xx4} src={require('../assets/images/4.png')} />

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
          <div style={{ display: 'flex', alignItems: 'center' }}>{children}</div>
        </div>
        <div className={styles.footer}>
          {props.version}&nbsp;
          <a style={{ color: '#fff', textDecoration: 'none' }} href="https://beian.miit.gov.cn/">
            {props.miitbeian}
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

export default connect(({ global, settings }) => ({
  ...settings,
  name: global.name,
  version: global.version,
  miitbeian: global.miitbeian,
}))(UserLayout);
