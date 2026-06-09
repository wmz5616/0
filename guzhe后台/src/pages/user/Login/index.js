import { setAuthority, setToken } from '@/utils/authority';
import { post } from '@/utils/request';
import { FormOutlined, LockOutlined, UserOutlined } from '@ant-design/icons';
import { history, useModel } from '@umijs/max';
import { Button, Col, Form, Input, message, Row } from 'antd';
import { useEffect, useState } from 'react';
import Captcha from 'react-captcha-code';
import ReactSimpleVerify from '../../components/ReactSimpleVerify';

import CryptoJS from 'crypto-js';
import styles from './index.less';

// 权限ID到路由的映射（与access.ts对应，按优先级排序）
const permissionRouteMap = [
  { ids: [1], route: '/home' },
  { ids: [85, 124, 154], route: '/shangjiaManagement/index' },
  { ids: [14], route: '/shangquanMsg/shangquanList' },
  { ids: [101, 107], route: '/OrderManagement/index' },
  { ids: [20, 138], route: '/TerminalEquipment/index' },
  { ids: [139], route: '/storefrontRentalManagement/index' },
  { ids: [7], route: '/reconciliationManagement/transactionRecord' },
  { ids: [8], route: '/MerchantManagement/index' },
  { ids: [9], route: '/NoticeNotice/index' },
  { ids: [10], route: '/userManagement/index' },
  { ids: [67, 73], route: '/AuthorityManagement/index' },
  { ids: [38, 57], route: '/SystemSettings/index' },
  { ids: [13], route: '/AppVerstion' },
];

// 根据权限列表获取第一个有权限的路由
function getFirstAuthorizedRoute(ruleIds) {
  if (!ruleIds || !ruleIds.length) {
    return '/home';
  }
  for (const item of permissionRouteMap) {
    if (item.ids.some(id => ruleIds.includes(id))) {
      return item.route;
    }
  }
  return '/home';
}

function encryptDES3(message, key, iv) {
  // 将消息和密钥转换为字节数组
  const messageBytes = CryptoJS.enc.Utf8.parse(message);
  const keyBytes = CryptoJS.enc.Utf8.parse(key);

  // 执行加密（使用DES3算法，CBC模式）
  const encrypted = CryptoJS.TripleDES.encrypt(messageBytes, keyBytes, {
    iv: CryptoJS.enc.Utf8.parse(iv),
    mode: CryptoJS.mode.CBC,
  });

  // 将加密结果转换为字符串
  const ciphertext = encrypted.toString();

  return ciphertext;
}

const Login = () => {
  const { fInfo } = useModel('global');
  const [sliderSuc, setSliderSuc] = useState(false);
  const [loading, setLoading] = useState(false);
  const [modes, setModes] = useState(false);
  const [captcha, setCaptcha] = useState('');

  useEffect(() => {
    if (
      navigator.userAgent.match(/Android/i) ||
      navigator.userAgent.match(/webOS/i) ||
      navigator.userAgent.match(/iPhone/i) ||
      navigator.userAgent.match(/iPad/i) ||
      navigator.userAgent.match(/iPod/i) ||
      navigator.userAgent.match(/BlackBerry/i) ||
      navigator.userAgent.match(/Windows Phone/i)
    ) {
      console.log(55555555);
      setModes(true);
      // 移动端适配
    } else {
      setModes(false);
      console.log(66666666666);
    }
  }, []);

  const onFinish = async (values) => {
    const encryptionKey = 'rPgEVmxhvxaWSdhOTgcnqJrp'; // 密(24位)
    const initializationVector = 62389533; // 初始化向量 (8位)

    const password = encryptDES3(
      values.password,
      encryptionKey,
      initializationVector,
    );
    const text = CryptoJS.TripleDES.encrypt(
      CryptoJS.enc.Utf8.parse(values.password),
      CryptoJS.enc.Utf8.parse('rPgEVmxhvxaWSdhOTgcnqJrp'),
      {
        mode: CryptoJS.mode.CBC,
        iv: CryptoJS.enc.Utf8.parse(initializationVector),
      },
    ).toString();
    const params = {
      account: values.account,
      password: text,
    };
    const query = location.query || {};
    const { isReception } = query;

    if (modes) {
      if (captcha != values.code) {
        message.info('验证码错误');
        return;
      }
    } else {
      if (!sliderSuc) {
        message.info('请完成滑块验证');
        return;
      }
    }

    setLoading(true);

    const res = await post('/guzhe/cas/login', params);

    if (res && res.code == 10000) {
      message.success(res.msg);
      setToken(res.data);
      setLoading(false);
      const ressd = await post('/guzhe/cas/info');
      console.log(ressd);
      window.localStorage.setItem('userInfo', JSON.stringify(ressd.data));
      setAuthority(ressd.data);
      // 跳转到第一个有权限的页面
      const firstRoute = getFirstAuthorizedRoute(ressd.data.ruleIds || []);
      // 使用 window.location.href 让 getInitialState 重新执行，获取最新权限
      window.location.href = firstRoute;
    } else {
      message.error(res?.msg);
      setLoading(false);
    }
  };

  const success = () => {
    setSliderSuc(true);
  };

  const handleClick = (captchaValue) => {
    setCaptcha(captchaValue);
  };

  const layout = {
    labelCol: {
      span: 0,
    },
    wrapperCol: {
      span: 24,
    },
  };
  const tailLayout = {
    wrapperCol: {
      offset: 0,
      span: 24,
    },
  };

  return (
    <div className={styles.container}>
      <Row>
        <Col span={12}>
          <img
            alt="sideImg"
            src={require('@/assets/images/loginpic.png')}
            width="100%"
            style={{ heigth: '100%' }}
          />
        </Col>
        <Col span={12} style={{ position: 'relative', height: 553 }}>
          <div className={styles.loginForm}>
            <div className={styles.mainTitle}>
              {fInfo.name}
              <span className={styles.version}>v1.0</span>
            </div>
            <Form className={styles.formStyle} {...layout} onFinish={onFinish}>
              <Form.Item
                name="account"
                rules={[
                  {
                    required: true,
                    message: '请输入账号!',
                  },
                ]}
              >
                <Input
                  placeholder="请输入账号"
                  className={styles.inputStyle}
                  prefix={<UserOutlined />}
                />
              </Form.Item>
              <Form.Item
                name="password"
                rules={[
                  {
                    required: true,
                    message: '请输入登录密码!',
                  },
                ]}
              >
                <Input
                  className={styles.inputStyle}
                  prefix={<LockOutlined />}
                  type="password"
                  placeholder="请输入登录密码"
                />
              </Form.Item>

              {modes ? (
                <Form.Item
                  name="code"
                  rules={[
                    {
                      required: true,
                      message: '请输入验证码!',
                    },
                  ]}
                >
                  <Input
                    className={styles.inputStyle}
                    prefix={<FormOutlined />}
                    placeholder="请输入验证码"
                    suffix={
                      <div className={styles.forget}>
                        <Captcha charNum={2} onChange={handleClick} />
                      </div>
                    }
                  />
                </Form.Item>
              ) : (
                <Form.Item className="mt20">
                  <ReactSimpleVerify
                    tips="拖动滑块验证"
                    width="324"
                    success={success}
                  />
                </Form.Item>
              )}

              <Form.Item {...tailLayout}>
                <Button
                  type="primary"
                  htmlType="submit"
                  className={styles.loginBtn}
                  loading={loading}
                >
                  登录
                </Button>
              </Form.Item>
            </Form>
          </div>
        </Col>
      </Row>
    </div>
  );
};

export default Login;
