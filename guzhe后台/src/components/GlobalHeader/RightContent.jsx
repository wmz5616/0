import { Tag } from 'antd';
import Avatar from './AvatarDropdown';
import styles from './index.less';

const ENVTagColor = {
  dev: 'orange',
  test: 'green',
  pre: '#87d068',
};

const GlobalHeaderRight = (props) => {
  let className = styles.right;
  return (
    <div className={styles.qwe}>
      <div>
        <div
          className={className}
          style={{
            cursor: 'pointer',
            display: 'flex',
            justifyContent: 'center',
            alignItems: 'center',
            height: 48,
            gap: 24,
            marginRight: 24,
          }}
        >
          <Avatar menu />
        </div>
      </div>
    </div>
  );
};

export default GlobalHeaderRight;
