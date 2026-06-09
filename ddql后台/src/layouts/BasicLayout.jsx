/**
 * Ant Design Pro v4 use `@ant-design/pro-layout` to handle Layout.
 *
 * @see You can view component api by: https://github.com/ant-design/ant-design-pro-layout
 */
import { ProLayout, DefaultFooter, SettingDrawer } from '@ant-design/pro-components';
import React, { useEffect, useMemo, useRef } from 'react';
import { Link, useIntl, connect, history } from 'umi';
import { GithubOutlined } from '@ant-design/icons';
import { Result, Button, message } from 'antd';
import Authorized from '@/utils/Authorized';
import RightContent from '@/components/GlobalHeader/RightContent';
import { getMatchMenu } from '@umijs/route-utils';
import favicon from '../assets/favicon.png';
import { setAuthority } from '../utils/authority';

const noMatch = (
  <Result
    status={403}
    title="403"
    subTitle="Sorry, you are not authorized to access this page."
    extra={
      <Button type="primary">
        <Link to="/user/login">Go Login</Link>
      </Button>
    }
  />
);

/** Use Authorized check all menu item */
const menuDataRender = (menuList) =>
  menuList.map((item) => {
    const localItem = {
      ...item,
      children: item.children ? menuDataRender(item.children) : undefined,
    };
    return Authorized.check(item.authority, localItem, null);
  });

const defaultFooterDom = (
  <DefaultFooter
    copyright={
      <div>
        Copyright © {new Date().getFullYear()} 东莞市南城文化服务中心 All Rights Reserved. &nbsp;
        <a style={{ color: '#fff' }} href="https://beian.miit.gov.cn/">
          粤ICP备2022109530号
        </a>
        <div>
          技术支持：
          <a
            href="https://www.bluewise.cc"
            target="_blank"
            rel="noreferrer"
            style={{ color: '#fff' }}
          >
            东莞市蓝睿网络科技有限公司
          </a>
        </div>
      </div>
    }
  />
);

const BasicLayout = (props) => {
  console.log(props);
  const {
    dispatch,
    children,
    settings,
    footer,
    location = {
      pathname: '/',
    },
  } = props;
  props.settings.headerHeight = 48;
  const menuDataRef = useRef([]);
  useEffect(() => {
    if (dispatch) {
      dispatch({
        type: 'user/fetchCurrent',
      });
      if (!localStorage.getItem('config')) {
        //获取系统设置
        dispatch({
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
              console.log(object);
              dispatch({
                type: 'global/saveConfig',
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
    }
  }, [dispatch]);
  /** Init variables */

  const handleMenuCollapse = (payload) => {
    if (dispatch) {
      dispatch({
        type: 'global/changeLayoutCollapsed',
        payload,
      });
    }
  }; // get children authority

  const authorized = useMemo(
    () =>
      getMatchMenu(location.pathname || '/', menuDataRef.current).pop() || {
        authority: undefined,
      },
    [location.pathname],
  );
  const { formatMessage } = useIntl();
  console.log(footer)
  return (
    <>
      <ProLayout
        title={footer.name}
        logo={footer.logo}
        formatMessage={formatMessage}
        {...props}
        {...settings}
        onCollapse={handleMenuCollapse}
        onMenuHeaderClick={() => history.push('/VenueManagement/index')}
        menuItemRender={(menuItemProps, defaultDom) => {
          // for(let i=0;i<document.getElementsByClassName('ant-menu').length;i++){
          //   for(let k=0;k<document.getElementsByClassName('ant-menu')[i].childNodes.length;k++){
          //         // console.log(document.getElementsByClassName('ant-menu')[i].childNodes[k].FI)
          //   }
          // }
          const authorityDate = JSON.parse(window.localStorage.getItem('antd-pro-authority'));
          // console.log(authorityDate)
          // console.log(menuItemProps)
          if (
            menuItemProps.isUrl ||
            !menuItemProps.path ||
            location.pathname === menuItemProps.path
          ) {
            return defaultDom;
          }
          if (authorityDate.filter((res) => res == parseInt(menuItemProps.key, 0)).length !== 0) {
            return <Link to={menuItemProps.path}>{defaultDom}</Link>;
          } else {
            return (
              <Link to={menuItemProps.path}>{defaultDom}</Link>
              // <Link disabled={true}>
              //   <div style={{ color: '#ccc' }}>{defaultDom}</div>{' '}
              // </Link>
            );
          }

          // return <Link to={menuItemProps.path}>{defaultDom}</Link>;
        }}
        breadcrumbRender={(routers = []) => [
          {
            path: '/SiteOperationsMamagement/siteData',
            breadcrumbName: formatMessage({
              id: 'menu.home',
            }),
          },
          ...routers,
        ]}
        itemRender={(route, params, routes, paths) => {
          const first = routes.indexOf(route) === 0;
          return first ? (
            <Link to={paths.join('/')}>{route.breadcrumbName}</Link>
          ) : (
            <span>{route.breadcrumbName}</span>
          );
        }}
        footerRender={() => {
          return (
            footer.version && (
              <div
                style={{
                  textAlign: 'center',
                  paddingBottom: '1%',
                  textAlign: 'center',
                }}
              >
                {/* Copyright © {new Date().getFullYear()} 东莞市松山湖体育管理服务中心 All Rights
              Reserved. &nbsp; */}
                {footer.version}&nbsp;&nbsp;
                <a
                  style={{ color: 'rgba(0, 0, 0, 0.85)' }}
                  href="https://beian.miit.gov.cn/#/Integrated/index"
                >
                  {/* 粤ICP备2024201897号 */}
                  {footer.miitbeian}
                </a>
                <div>
                  技术支持：
                  <a
                    href="https://www.bluewise.cc/"
                    target="_blank"
                    rel="noreferrer"
                    style={{ color: '#000' }}
                  >
                    东莞市蓝睿网络科技有限公司
                  </a>
                </div>
              </div>
            )
          );
        }}
        menuDataRender={menuDataRender}
        rightContentRender={() => <RightContent />}
        postMenuData={(menuData) => {
          menuDataRef.current = menuData || [];
          return menuData || [];
        }}
      >
        <Authorized authority={authorized.authority} noMatch={noMatch}>
          <div className="top-fixed"></div>
          {children}
        </Authorized>
      </ProLayout>
      {/* <SettingDrawer
        settings={settings}
        onSettingChange={(config) =>
          dispatch({
            type: 'settings/changeSetting',
            payload: config,
          })
        }
      /> */}
    </>
  );
};

export default connect(({ global, settings }) => ({
  collapsed: global.collapsed,
  settings,
  footer: global.footer,
}))(BasicLayout);
