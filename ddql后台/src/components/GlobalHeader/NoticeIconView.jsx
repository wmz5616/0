import React, { Component } from 'react';
import { connect } from 'umi';
import { Tag, message } from 'antd';
import groupBy from 'lodash/groupBy';
import moment from 'moment';
import NoticeIcon from '../NoticeIcon';
import styles from './index.less';

class GlobalHeaderRight extends Component {


  render() {
    return (
      <div >
      <NoticeIcon
        className={styles.action}
        // style={{position:'fixed',top:20}}
      >
        <NoticeIcon
          // tabKey="notification"
          // title="用户反馈"
          // emptyText="你已查看用户反馈"
        />
      
      
      </NoticeIcon>
      </div>
    );
  }
}

export default connect(({ user, global, loading }) => ({
}))(GlobalHeaderRight);
