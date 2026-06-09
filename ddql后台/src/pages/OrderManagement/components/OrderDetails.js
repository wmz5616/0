import React from 'react';
import { PageContainer } from '@ant-design/pro-layout';
import { Button, Input, Form, message, Modal, Radio } from 'antd';
import { getToken } from '@/utils/authority';
const { TextArea } = Input;
// 将connect导入
import { history, connect, Link, Redirect } from 'umi';

class DataConnection extends React.Component {
  formRef = React.createRef();
  formRefs = React.createRef();
  formRefss = React.createRef();
  state = {};

  componentDidMount() {
    this.getData();
  }

  getData = () => {
    this.setState(
      {
        spinning: true,
      },
      () => {
        //列表
        this.props.dispatch({
          type: 'myModel/getSetData',
          payload: {
            id: this.props.Id,
          },
          url: `/api/admin/order/refund/info`,
          method: 'GET',
          myData: (res) => {
            if (res && res.code === 200) {
              console.log(res);
              this.setState({
                info: res.data,
              });
            } else {
              message.error(res.message);
              // this.setState({ isSelectForm: true });
            }
          },
        });
      },
    );
  };

  handleCancel = () => {
    const { handleCancels } = this.props;

    handleCancels();
  };

  handleCancelss = () => {
    const { handleCancels } = this.props;

    this.setState(
      {
        a: true,
      },
      () => {
        handleCancels();
      },
    );
  };

  handleOk = () => {
    this.setState({
      visible: true,
    });
  };

  handleCancelz = () => {
    this.setState({
      visible: false,
    });
  };

  handleOks = () => {
    this.formRefs.current.validateFields().then((values) => {
      this.props.dispatch({
        type: 'myModel/getSetData',
        payload: {
          id: this.props.Id,
          status: values.status,
          reason: this.state.reason,
        },
        // dataName: 'developerListData',
        method: 'POST',
        url: `/api/admin/refund/audit`,
        myData: (res) => {
          if (res.code === 200) {
            message.success(res.message);
            this.setState({
              NewRoles: false,
              selectedRowKeys: [],
            });
            const { handleCancels, getData } = this.props;
            handleCancels();
            getData();
          } else {
            message.success(res.message);
          }
        },
      });
    });
  };

  render() {
    const { info = {}, visible } = this.state;
    const { images = [] } = info;
    if (this.state.a) {
       history.push(`/OrderManagement/OrderDetails?id=${this.state.info.order_id}&idd=${this.state.info.id}`)
      window.location.reload()
    }
    return (
      <>
        <Modal
          title="申请详情"
          visible
          onOk={this.handleOk}
          onCancel={this.handleCancel}
          destroyOnClose
          footer={
            info.status == 2 || info.status == 3
              ? [
                  <Button key="submit" type="primary" onClick={this.handleCancel}>
                    确定
                  </Button>,
                ]
              : [
                  <Button key="back" onClick={this.handleCancel}>
                    取消
                  </Button>,
                  <Button key="submit" type="primary" onClick={this.handleOk}>
                    审核
                  </Button>,
                ]
          }
        >
          <div style={{ lineHeight: '36px' }}>
            <div style={{ marginLeft: 65 }}>ID：{info.id}</div>
            <div style={{ marginLeft: 22 }}>
              订单编号： <a onClick={this.handleCancelss}> {info.order_no}</a>
            </div>
            <div style={{ marginLeft: 50 }}>场所：{info.stadium && info.stadium.name}</div>
            <div style={{ marginLeft: 50 }}>场馆：{info.gym && info.gym.name}</div>
            <div style={{ marginLeft: 22 }}>
              订单类型：
              <span>
                {info.order_type == 1 && <span>订场地</span>}
                {info.order_type == 2 && <span>订门票</span>}
              </span>
            </div>
            <div>订单金额(元)：{info.amount}</div>
            <div style={{ marginLeft: 36 }}>
              下单人：{info.order_user && info.order_user.username}(
              {info.order_user && info.order_user.phone})
            </div>
            <div style={{ marginLeft: 22 }}>
              申请状态：{' '}
              <span>
                {info.status == 2 && <span className="luSe">已通过</span>}
                {info.status == 1 && <a>待审核</a>}
                {info.status == 3 && <span className="huangse">已驳回</span>}
              </span>
            </div>
            <div style={{ marginLeft: 36 }}>
              申请人：{info.apply_user && info.apply_user.username}(
              {info.apply_user && info.apply_user.phone})
            </div>
            <div style={{ marginLeft: 22 }}>申请原因：{info.reason}</div>
            <div style={{ marginLeft: 50, marginTop: 10, marginBottom: 10 }}>
              图片：
              <>
                {images[0] != '' && (
                  <>
                    {images.map((res) => {
                      return (
                        <img
                          onClick={() => this.showModalzz(res)}
                          key={res}
                          src={`${res}`}
                          alt=""
                          style={{ width: 80, height: 80, objectFit: 'contain', marginRight: 10 }}
                        />
                      );
                    })}
                  </>
                )}
              </>
            </div>
            <div style={{ marginLeft: 22 }}>申请时间：{info.created_at}</div>
          </div>
        </Modal>
        <Modal title="审核" visible={visible} onOk={this.handleOks} onCancel={this.handleCancelz}>
          <Form ref={this.formRefs}>
            <Form.Item
              label="审核结果"
              name="status"
              initialValue={2}
              rules={[{ required: true, message: '请选择!' }]}
            >
              <Radio.Group>
                <Radio value={2}>通过</Radio>
                <Radio value={3}>驳回</Radio>
              </Radio.Group>
            </Form.Item>
            <Form.Item label="审核意见" name="reason">
              <TextArea rows={4} placeholder="请输入" />
            </Form.Item>
          </Form>
        </Modal>
      </>
    );
  }
}

// 7
// 绑定到本页面，此处就可以拿到请求的值，
// allModels所有models集合，mapping是具体的model的命名空间，mappingData命名空间里面你想取的值
export default connect((allModels) => ({}))(DataConnection);
