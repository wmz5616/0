import React from 'react';
import { LockOutlined,UserOutlined } from '@ant-design/icons';
import { Form, Input, Button, Row, Col, message } from 'antd';
import { connect, history } from 'umi';
import styles from '../User/login/index.less';
import loginPic from '../../assets/images/loginpic.png';

class ChangePassowrd extends React.Component {
  state = {};

  formRef = React.createRef();

  componentDidMount() {
    // console.log(this.props);
  }

  onFinish = (values) => {
    // console.log('Success:', values);
    if (values.password !== values.passwordConfirm) {
      message.error('两次密码输入不一致');
      return;
    }
    const {
      location: {
        state: { code, number },
      },
    } = this.props;
    const { dispatch } = this.props;
    dispatch({
      type: 'login/updatePassword',
      payload: {
        password: values.password,
        passwordConfirm: values.passwordConfirm,
        code,
        number,
      },
    });
  };

  onFinishFailed = (errorInfo) => {
    console.log('Failed:', errorInfo);
  };

  render() {
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
            <img alt="sideImg" src={loginPic} width="100%" />
          </Col>
          <Col span={12}>
            <div className={styles.loginForm}>
              <div className={styles.mainTitle}>
                都动起来<span className={styles.version}>v3.0</span>
              </div>
              <div className={styles.foundTitle}>重置密码</div>
              <Form
                ref={this.formRef}
                colon={false}
                className={styles.formStyle}
                {...layout}
                // name="basic"
                onFinish={this.onFinish}
                onFinishFailed={this.onFinishFailed}
              >
                <Form.Item
                  labelCol={{ span: 4 }}
                  className="forgetAccount"
                  name="account"
                  rules={[
                    {
                      required: true,
                      message: `请输入`,
                    },
                  ]}
                >
                  <Input
                    placeholder={`请输入账号`}
                    className={styles.forgetInput}
                    prefix={<UserOutlined />}
                  />
                </Form.Item>

                <Form.Item
                  name="password"
                  rules={[
                    {
                      required: true,
                      message: '请输入新密码！',
                    },
                  ]}
                >
                  <Input
                    type="password"
                    placeholder="请输入新密码"
                    className={styles.forgetInput}
                    prefix={<LockOutlined />}
                  />
                </Form.Item>
                <Form.Item
                  name="passwordConfirm"
                  rules={[
                    {
                      required: true,
                      message: '请确认新密码!',
                    },
                  ]}
                >
                  <Input
                    placeholder="请确认新密码"
                    type="password"
                    className={styles.forgetInput}
                    prefix={<LockOutlined />}
                  />
                </Form.Item>
                <div className={styles.tipText}>8-20个字符，必须同时包含字母和数字。</div>
                <Form.Item {...tailLayout}>
                  <Button type="primary" htmlType="submit" className={styles.loginBtn}>
                    确认重置
                  </Button>
                  <Button
                    onClick={() => {
                      history.push('/user/login');
                    }}
                    className={styles.returnLogin}
                  >
                    返回登录
                  </Button>
                </Form.Item>
              </Form>
            </div>
          </Col>
        </Row>
      </div>
    );
  }
}

export default connect()(ChangePassowrd);
