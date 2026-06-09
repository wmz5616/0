import { Button, Form, Input, message, Modal, Radio, InputNumber } from 'antd';
import React from 'react';
const { TextArea } = Input;
// 将connect导入
import { post } from '@/utils/request';
import { history, Link } from 'umi';

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

  getData = async () => {
    const res = await post(`/guzhe/product_order/refund/apply/info`, {
      searchId: this.props.applyInfo.id,
    });
    if (res && res.code == 10000) {
      this.setState({
        applyInfo: res.data,
      });
    } else {
      message.error(res?.msg);
    }
  };

  handleCancel = () => {
    const { handleCancels } = this.props;
    handleCancels();
  };

  handleOk = () => {
    const { applyInfo } = this.state;
    this.setState({
      visible: true,
      sss: 2, //默认审核通过
    }, () => {
      this.formRefs.current.setFieldsValue({
        refundAmount: applyInfo?.applyInfo.refundAmount ? applyInfo?.applyInfo.refundAmount / 100 : applyInfo?.orderInfo.amount / 100,
        remark: applyInfo?.applyInfo.remark,
        // status: applyInfo?.applyInfo.status,
        status: 2, //默认审核通过
      })
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
    this.formRefs.current.validateFields().then(async (values) => {
      const res = await post(`/guzhe/product_order/refund/audit`, {
        applyId: this.state.applyInfo.applyInfo.id,
        status: values.status,
        remark: values.remark,
        refundAmount: values.refundAmount * 100,
      });
      if (res.code == 10000) {
        message.success(res.msg);
        this.setState({
          visible: false,
        });
        this.getData();
      } else {
        message.error(res?.msg);
      }
    });
  };

  render() {
    const { applyInfo, visible } = this.state;

    return (
      <>
        <Modal
          title="申请详情"
          visible
          onOk={this.handleOk}
          onCancel={this.handleCancel}
          destroyOnClose
          footer={
            applyInfo?.applyInfo.status == 1
              ? [
                <Button key="back" onClick={this.handleCancel}>
                  取消
                </Button>,
                <Button key="submit" type="primary" onClick={this.handleOk}>
                  审核
                </Button>,
              ]
              : [
                <Button
                  key="submit"
                  type="primary"
                  onClick={this.handleCancel}
                >
                  确定
                </Button>,
              ]
          }
        >
          <div style={{ lineHeight: '36px' }}>
            <div>ID：{applyInfo?.orderInfo.id}</div>
            <div>
              订单编号：
              {/* <Link
                to={`/OrderManagement/OrderDetails?orderNo=${applyInfo?.orderInfo.orderNo}`}
              >
                {applyInfo?.orderInfo.orderNo}
              </Link> */}
              {applyInfo?.orderInfo.orderNo}
            </div>
            <div>商品：{applyInfo?.orderInfo.productName}</div>
            <div>数量：{applyInfo?.orderInfo.num}</div>
            <div>订单金额(元)：{applyInfo?.orderInfo.orderPrice / 100 || 0}元</div>
            <div>
              下单人：{applyInfo?.orderInfo.nickName}（
              {applyInfo?.orderInfo.phone}）
            </div>
            <div>
              申请状态：
              <span>
                {applyInfo?.applyInfo.status == 2 && (
                  <span className="luSe">已通过</span>
                )}
                {applyInfo?.applyInfo.status == 1 && (
                  <span className="clickFont">待审核</span>
                )}
                {applyInfo?.applyInfo.status == 3 && (
                  <span className="red">已驳回</span>
                )}
              </span>
            </div>
            {applyInfo?.applyInfo.name && (
              <div>
                审核人：{applyInfo?.applyInfo.name}（
                {applyInfo?.applyInfo.account}）
              </div>
            )}
            <div>申请原因：{applyInfo?.applyInfo.reason}</div>
            {applyInfo?.applyInfo.images && (
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
                          style={{
                            width: 80,
                            height: 80,
                            objectFit: 'contain',
                            marginRight: 10,
                          }}
                        />
                      );
                    })}
                </>
              </div>)}
            <div>申请时间：{applyInfo?.applyInfo.createTime}</div>
            {applyInfo?.applyInfo?.auditRemark && (
              <div>{applyInfo?.applyInfo.status == 3 ? '驳回' : '通过'}原因：{applyInfo?.applyInfo?.auditRemark}</div>
            )}
          </div>
        </Modal>
        <Modal
          title="审核"
          open={visible}
          onOk={this.handleOks}
          onCancel={this.handleCancelz}
        >
          <Form ref={this.formRefs}>
            <Form.Item
              label={
                <span>
                  <span style={{ color: 'red' }}>*</span>退款金额
                </span>
              }
            >
              <Form.Item
                name="refundAmount"
                noStyle
                rules={[{ required: true, message: '请输入退款金额!' }]}
              >
                <InputNumber min={0} max={applyInfo.orderInfo.amount / 100} />
              </Form.Item>
              <div style={{ color: '#ccc' }}>
                订单总金额：{applyInfo.orderInfo.amount / 100 || 0}元
              </div>
            </Form.Item>
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
              rules={[
                {
                  required: this.state.sss == 3 ? true : false,
                  message: '请输入',
                },
              ]}
            >
              <TextArea rows={4} placeholder="请输入" />
            </Form.Item>
          </Form>
        </Modal>
      </>
    );
  }
}

export default DataConnection;
