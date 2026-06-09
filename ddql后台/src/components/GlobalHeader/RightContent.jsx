import { Tooltip, Tag } from 'antd';
import { QuestionCircleOutlined } from '@ant-design/icons';
import React from 'react';
import { connect, SelectLang, history ,Link} from 'umi';
import Avatar from './AvatarDropdown';
import HeaderSearch from '../HeaderSearch';
import styles from './index.less';
import NoticeIconView from './NoticeIconView';
// import xxx  from '../../../public/111.pdf'

const ENVTagColor = {
  dev: 'orange',
  test: 'green',
  pre: '#87d068',
};

const GlobalHeaderRight = (props) => {
  const { theme, layout } = props;
  let className = styles.right;

  if (theme === 'dark' && layout === 'top') {
    className = `${styles.right}  ${styles.dark}`;
  }

  return (
    <div className={styles.qwe}>
      <div >
    <div className={className} style={{ cursor: 'pointer', display: 'flex', justifyContent: 'center', alignItems: 'center', height: 48,gap: 24,marginRight: 24 }}>
{/*    
      <Tooltip title="使用文档">
        <a
          style={{
            color: 'inherit',
          }}
          target="_blank"
        href='https://kdocs.cn/l/ciUh2SuHiI0l'
          rel="noopener noreferrer"
          className={styles.action}
        >
         
         <QuestionCircleOutlined />
        </a>
      </Tooltip>
     

      <Tooltip title="消息中心">
      <NoticeIconView />
      </Tooltip> */}


      <Avatar menu />
      {REACT_APP_ENV && (
        <span>
          <Tag color={ENVTagColor[REACT_APP_ENV]}>{REACT_APP_ENV}</Tag>
        </span>
      )}
      {/* <SelectLang className={styles.action} /> */}
    </div>
    </div>
    </div>
  );
};

export default connect(({ settings }) => ({
  theme: settings.navTheme,
  layout: settings.layout,
}))(GlobalHeaderRight);
