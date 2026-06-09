import { LogoutOutlined } from '@ant-design/icons';
import { useModel } from '@umijs/max';
import { Avatar, Menu } from 'antd';
import { history } from 'umi';
import HeaderDropdown from '../HeaderDropdown';
import { getPageQuery } from '@/utils/utils';
import styles from './index.less';
import { stringify } from 'querystring';

const AvatarDropdown = (props) => {
  const onMenuClick = (event) => {
    const { key } = event;

    if (key === 'logout') {
      window.localStorage.clear();
      const { redirect } = getPageQuery(); // Note: There may be security issues, please note

      if (window.location.pathname !== '/user/login' && !redirect) {
           window.location.href='/user/login?t='+ Date.now()
        // history.replace({
        //   pathname: '/user/login',
        //   search: stringify({
        //     redirect: window.location.href,
        //   }),
        // });
      }
      return;
    }

    history.push(`/account/${key}`);
  };

  const userInfo = localStorage.getItem('userInfo')
    ? JSON.parse(localStorage.getItem('userInfo'))
    : {};
  const { currentUser } = useModel('global');

  const menuHeaderDropdown = (
    <Menu className={styles.menu} selectedKeys={[]} onClick={onMenuClick}>
      <Menu.Item key="logout">
        <LogoutOutlined />
        退出登录
      </Menu.Item>
    </Menu>
  );

  return currentUser && currentUser.name ? (
    <HeaderDropdown overlay={menuHeaderDropdown}>
      <span className={`${styles.action} ${styles.account}`}>
        <Avatar
          size="small"
          className={styles.avatar}
          src="https://zos.alipayobjects.com/rmsportal/ODTLcjxAfvqbxHnVXCYX.png"
          alt="avatar"
        />
        <span className={`${styles.name} anticon`}>{userInfo.name}</span>
      </span>
    </HeaderDropdown>
  ) : (
    <HeaderDropdown overlay={menuHeaderDropdown}>
      <span className={`${styles.action} ${styles.account}`}>
        <Avatar
          size="small"
          className={styles.avatar}
          src="https://zos.alipayobjects.com/rmsportal/ODTLcjxAfvqbxHnVXCYX.png"
          alt="avatar"
        />
        <span className={`${styles.name} anticon`}>{userInfo.name}</span>
      </span>
    </HeaderDropdown>
  );
};

export default AvatarDropdown;
