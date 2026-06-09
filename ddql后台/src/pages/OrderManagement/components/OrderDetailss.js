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
  state = {
    applyInfo: { orderInfo: {}, applyInfo: {} },
  };

  componentDidMount() {
    this.getData();
  }

  getData = () => {
    this.props.dispatch({
      type: 'myModel/getSetData',
      payload: {
        searchId: this.props.applyInfo.id,
      },
      url: `/ddql/order/exchange/refund/apply/info`,
      method: 'POST',
      myData: (res) => {
        if (res && res.code === 10000) {
          this.setState({
            applyInfo: res.data,
          });
        } else {
          message.error(res.msg);
        }
      },
    });
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
    this.formRefs.current.resetFields();
  };

  onChangezz = (e) => {
    this.setState({
      sss: e.target.value,
    });
  };

  handleOks = () => {
    this.formRefs.current.validateFields().then((values) => {
      this.props.dispatch({
        type: 'myModel/getSetData',
        payload: {
          applyId: this.state.applyInfo.applyInfo.id,
          status: values.status,
          remark: values.remark,
          refundAmount: this.state.applyInfo.orderInfo.amount,
        },
        method: 'POST',
        url: `/ddql/order/exchange/refund/audit`,
        myData: (res) => {
          if (res.code === 10000) {
            message.success(res.msg);
            this.setState({
              visible: false,
            });
            this.getData();
            // handleCancels();
            getData();
          } else {
            message.error(res.msg);
          }
        },
      });
    });
  };

  render() {
    const { applyInfo, visible } = this.state;

    if (this.state.a) {
      history.push(`/OrderManagement/OrderDetails?id=${this.state.applyInfo.orderNo}`);
      window.location.reload();
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
            applyInfo?.applyInfo.status == 1 || applyInfo?.applyInfo.status == 3
              ? [
                  <Button key="back" onClick={this.handleCancel}>
                    取消
                  </Button>,
                  <Button key="submit" type="primary" onClick={this.handleOk}>
                    审核
                  </Button>,
                ]
              : [
                  <Button key="submit" type="primary" onClick={this.handleCancel}>
                    确定
                  </Button>,
                ]
          }
        >
          <div style={{ lineHeight: '36px' }}>
            <div>ID：{applyInfo?.orderInfo.id}</div>
            <div>
              订单编号：
              {/* <a onClick={this.handleCancelss}> 
                </a> */}
              <Link to={`/OrderManagement/OrderDetails?orderNo=${applyInfo?.orderNo}`}>
                {' '}
                {applyInfo?.orderInfo.orderNo}
              </Link>
            </div>
            <div>订单金额(元)：{applyInfo?.orderInfo.orderPrice}</div>
            <div>
              下单人：{applyInfo?.orderInfo.nickName}（{applyInfo?.orderInfo.phone}）
            </div>
            <div>
              申请状态：{' '}
              <span>
                {applyInfo?.applyInfo.status == 2 && <span className="luSe">已通过</span>}
                {applyInfo?.applyInfo.status == 1 && <span className="clickFont">待审核</span>}
                {applyInfo?.applyInfo.status == 3 && <span className="red">已驳回</span>}
              </span>
            </div>
            <div>
              审核人：{applyInfo?.applyInfo.name}（{applyInfo?.applyInfo.account}）
            </div>
            <div>申请原因：{applyInfo?.applyInfo.reason}</div>
            <div>
              图片：
              <>
                {applyInfo.applyInfo &&
                  applyInfo.applyInfo.images &&
                  applyInfo.applyInfo.images.split(',').map((res) => {
                    return (
                      <img
                        onClick={() => window.open(res)}
                        key={res}
                        src={`${res}`}
                        alt=""
                        style={{ width: 80, height: 80, objectFit: 'contain', marginRight: 10 }}
                      />
                    );
                  })}
              </>
            </div>
            <div>申请时间：{applyInfo?.applyInfo.createTime}</div>
          </div>
        </Modal>
        <Modal title="审核" open={visible} onOk={this.handleOks} onCancel={this.handleCancelz}>
          <Form ref={this.formRefs}>
            <Form.Item
              label="审核结果"
              name="status"
              initialValue={2}
              rules={[{ required: true, message: '请选择!' }]}
            >
              <Radio.Group onChange={this.onChangezz}>
                <Radio value={2}>通过</Radio>
                <Radio value={3}>驳回</Radio>
              </Radio.Group>
            </Form.Item>
            <Form.Item
              label="审核意见"
              name="remark"
              rules={[{ required: this.state.sss == 2 ? false : true, message: '请输入' }]}
            >
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
