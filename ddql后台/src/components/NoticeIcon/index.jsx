import { BellOutlined } from '@ant-design/icons';
import { Badge, Spin, Tabs,Avatar  } from 'antd';
import useMergedState from 'rc-util/es/hooks/useMergedState';
import React from 'react';
import classNames from 'classnames';
import NoticeList from './NoticeList';
import HeaderDropdown from '../HeaderDropdown';
import styles from './index.less';
import { connect } from 'umi';
const { TabPane } = Tabs;

const NoticeIcon = (props) => {
  const { className, count, bell,counts,countss} = props;
  console.log(countss,counts,222)
  const [visible, setVisible] = useMergedState(false, {
    value: props.popupVisible,
    onChange: props.onPopupVisibleChange,
  });
  const noticeButtonClass = classNames(className, styles.noticeButton);
  const NoticeBellIcon = bell ||  
  <Badge  size="small" count={parseInt(countss,10)+parseInt(counts,10)}>
  <BellOutlined className={styles.icon} />
</Badge>
{/* <BellOutlined className={styles.icon} /> */}
;
  const getNotificationBox = () => {
    const {
      children,
      loading,
      onClear,
      onTabChange,
      onItemClick,
      onViewMore,
      clearText,
      viewMoreText,
    } = props;

    if (!children) {
      return null;
    }

    const panes = [];
    React.Children.forEach(children, (child) => {
      if (!child) {
        return;
      }

      const { list, title, count, tabKey, showClear, showViewMore } = child.props;
      const len = list && list.length ? list.length : 0;
      const msgCount = count || count === 0 ? count : len;
      const tabTitle = msgCount > 0 ? `${title} (${msgCount})` : title;
      panes.push(
        <TabPane tab={tabTitle} key={tabKey} style={{backgroundColor:'#fff',marginTop:'-18px'}}>
          {visible && 
          <div> <NoticeList/></div>
         
          }
        
        </TabPane>,
      );
    });
    return (
 
        <Tabs className={styles.tabs} onChange={onTabChange}>
          {panes}
        </Tabs>
  
    );
  };
  const notificationBox = getNotificationBox();
  const trigger = (
    <span
      className={classNames(noticeButtonClass, {
        opened: visible,
      })}
    >
      <Badge
        count={count}
        style={{
          boxShadow: 'none',
        }}
        className={styles.badge}
      >
        {NoticeBellIcon}
      </Badge>
    </span>
  );

  if (!notificationBox) {
    return trigger;
  }

  return (
    <HeaderDropdown
      placement="bottomRight"
      overlay={notificationBox}
      overlayClassName={styles.popover}
      trigger={['click']}
      visible={visible}
      onVisibleChange={setVisible}
      style={{backgroundColor:'ActiveCaption'}}
    >
      {trigger}
    </HeaderDropdown>
  );
};

NoticeIcon.defaultProps = {
  emptyImage: 'https://gw.alipayobjects.com/zos/rmsportal/wAhyIChODzsoKIOBHcBk.svg',
};
NoticeIcon.Tab = NoticeList;
export default connect(({ global, settings }) => ({
  countss: global.countss,
  counts: global.counts,
}))(NoticeIcon);

