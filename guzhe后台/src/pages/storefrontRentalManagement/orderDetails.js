import { post } from '@/utils/request';
import { PageContainer } from '@ant-design/pro-layout';
import { UploadOutlined } from '@ant-design/icons';
import { history } from '@umijs/max';
import {
    Button,
    Col,
    Divider,
    Form,
    Input,
    InputNumber,
    message,
    Modal,
    Row,
    Spin,
    Table,
    Radio,
    Upload,
} from 'antd';
import React from 'react';
import { urlName } from '@/utils/utils';
const { TextArea } = Input;
//兑换订单
class orderDetails extends React.Component {
    formRefz = React.createRef();
    state = {
        spinning: false,
        orderNo: undefined,
        // 订单详情
        orderDetail: {},
        auitResult: 1,
        modalType: 'audit',
        reviewVisible: false,
        fileList: [],
        // 订单日志
        orderLogs: [],
    };

    componentDidMount() {
        const searchParams = new URLSearchParams(history.location?.search || '');
        const id = searchParams.get('id');
        if (id) {
            this.setState(
                {
                    id,
                },
                () => {
                    this.getData();
                },
            );
        }
    }

    handleUploadChange =
        (type) =>
            ({ file, fileList }) => {
                // 处理文件删除的情况
                if (file.status === 'removed') {
                    this.setState({ [type]: fileList });
                    return;
                }
                this.setState({ [type]: fileList }, () => {
                    const { response = {} } = file;
                    if (response.code === 10000) {
                        const data = this.state[type];
                        if (data[data.length - 1]) {
                            console.log(data[data.length - 1]);
                            data[data.length - 1].response.data.url =
                                urlName + data[data.length - 1].response.data.url;
                            this.setState({
                                [type]: data,
                            });
                        } else {
                            this.setState({
                                [type]: [],
                            });
                        }
                    } else {
                        if (response.msg) {
                            message.info(response.msg);
                        }
                    }
                });
            };


    getData = () => {
        this.setState(
            {
                spinning: true,
            },
            async () => {
                //基本信息
                const res = await post(`/guzhe/screen_order/info`, {
                    orderId: this.state.id,
                });
                this.setState({
                    spinning: false,
                });
                if (res && res.code == 10000) {
                    const order = res.data;
                    this.setState({
                        orderDetail: order,
                        orderLogs: order.operationRecords,
                    });
                } else {
                    message.error(res?.msg);
                }
            },
        );
    };

    handleOk = () => {
        this.formRefz.current.validateFields().then(async (values) => {
            const res = await post(this.state.modalType == 'audit' ? `/guzhe/screen_order/audit` : `/guzhe/screen_order/cancel`, {
                orderId: this.state.id,
                result: this.state.auitResult || undefined,
                [this.state.modalType == 'audit' ? 'remark' : 'cancelReason']: values.auditOpinion,
                fileUrl: this.state.fileList.map((file) => file.response?.data?.url).join(',') || undefined,
            })

            if (res && res.code == 10000) {
                message.success(res.msg);
                this.formRefz.current.resetFields()
                this.setState({
                    reviewVisible: false,
                    fileList: [],
                }, () => {
                    this.getData()
                }
                );
            } else {
                message.error(res?.msg);
            }
        });
    };


    showModal = (type) => {
        const auitResult = type == 'cancel' ? 0 : 1;
        this.setState({
            modalType: type,
            reviewVisible: true,
            auitResult,
        }, () => {
            this.formRefz.current.setFieldsValue({
                auitResult,
            });
        });
    }

    render() {
        const {
            orderDetail = {},
            orderLogs = [],
        } = this.state;


        //操作记录
        const columnsss = [
            {
                title: 'ID',
                dataIndex: 'id',
            },
            {
                title: '操作时间',
                dataIndex: 'operationTime',
            },
            {
                title: '操作',
                dataIndex: 'operationTypeText',
            },

            {
                title: '详情',
                dataIndex: 'operatorText',
                render: (text, record) => {
                    const getRemarkText = () => {
                        if (!record.operationRemark) return '';
                        if (record.operationType === 3) {
                            return record.operationResult === 1
                                ? `审核意见：${record.operationRemark}`
                                : `驳回原因：${record.operationRemark}`;
                        }
                        if (record.operationType === 4) {
                            return `撤销原因：${record.operationRemark}`;
                        }
                        return '';
                    };

                    return (
                        <div style={{ display: 'flex', gap: 10 }}>
                            <span>{text}</span>
                            <span>{getRemarkText()}</span>
                            {record.fileUrl && (
                                <span>
                                    <a href={record.fileUrl} target="_blank" rel="noopener noreferrer">
                                        查看文件
                                    </a>
                                </span>
                            )}
                        </div>
                    );
                }

            },
        ];

        const routes = [
            {
                // path:`VenueDetails?id=${id}` ,
                breadcrumbName: '首页',
            },
            {
                // path:`VenueDetails?id=${id}` ,
                breadcrumbName: '订单管理',
            },
            {
                // path: `/VenueDetails?id=${id}`,
                breadcrumbName: '订单详情',
            },
        ];

        return (
            <PageContainer
                header={{
                    title: ``,
                    breadcrumb: {
                        itemRender: this.itemRender,
                        routes,
                    },
                }}
            >
                <Spin spinning={this.state.spinning}>
                    <div style={{ backgroundColor: '#fff', padding: 30 }}>
                        <Row gutter={16} style={{ lineHeight: '32px' }}>
                            <Col className="gutter-row" span={24}>
                                <div
                                    style={{ display: 'flex', justifyContent: 'space-between' }}
                                >
                                    <h3>店位订单基本信息</h3>
                                    <div style={{ display: 'flex', gap: 20 }}>
                                        {
                                            orderDetail.status == 0 && <Button type="primary" onClick={() => this.showModal('audit')}>审核</Button>
                                        }
                                        {
                                            (orderDetail.status == 1 || orderDetail.status == 2 || orderDetail.status == 3) && <Button type="primary" danger onClick={() => this.showModal('cancel')}>撤销</Button>
                                        }
                                        <Button onClick={() => window.history.back()}>返回</Button>
                                    </div>
                                </div>
                                <Row gutter={16}>
                                    <Col className="gutter-row" span={6}>
                                        <div>订单ID：{orderDetail.orderId}</div>
                                    </Col>
                                    <Col className="gutter-row" span={6}>
                                        <div>支付时间：{orderDetail.orderTime}</div>
                                    </Col>
                                    <Col className="gutter-row" span={6}>
                                        <div>设备编号：{orderDetail.serialNumber}</div>
                                    </Col>
                                    <Col className="gutter-row" span={6}>
                                        <div>所属商超：{orderDetail.businessCircleName}</div>
                                    </Col>
                                    <Col className="gutter-row" span={6}>
                                        <div>下单商家：{orderDetail.shopName}</div>
                                    </Col>
                                    <Col className="gutter-row" span={6}>
                                        <div>
                                            下单人：
                                            <span>
                                                {orderDetail.nickName}（{orderDetail.phone}）
                                            </span>
                                        </div>
                                    </Col>
                                    <Col className="gutter-row" span={6}>
                                        <div>
                                            订单金额：
                                            <span>{orderDetail.totalAmount / 100 || 0}元</span>
                                        </div>
                                    </Col>
                                    <Col className="gutter-row" span={6}>
                                        <div>订单号：{orderDetail.orderNo}</div>
                                    </Col>
                                    <Col className="gutter-row" span={6}>
                                        <div>
                                            订单状态：
                                            <span
                                                style={{
                                                    color:
                                                        orderDetail.status == 0
                                                            ? '#2e99ff'
                                                            : orderDetail.status == 1
                                                                ? '#f79a71'
                                                                : orderDetail.status == 2
                                                                    ? 'red'
                                                                    : orderDetail.status == 3
                                                                        ? '#27b43e '
                                                                        : orderDetail.status == 4
                                                                            ? '#8b8b8b'
                                                                            : orderDetail.status == 5
                                                                                ? '#8b8b8b'
                                                                                : '',
                                                }}
                                            >
                                                {orderDetail.status == 0
                                                    ? '待确认'
                                                    : orderDetail.status == 1
                                                        ? '待生效'
                                                        : orderDetail.status == 2
                                                            ? '生效中'
                                                            : orderDetail.status == 3
                                                                ? '已完成'
                                                                : orderDetail.status == 4
                                                                    ? '已驳回'
                                                                    : orderDetail.status == 5
                                                                        ? '已撤销'
                                                                        : ''}
                                            </span>
                                        </div>
                                    </Col>
                                    <Col className="gutter-row" span={6}>
                                        <div>租用月份：{orderDetail.rentalMonths}</div>
                                    </Col>

                                </Row>
                            </Col>
                        </Row>
                        <Divider />

                        <h3 style={{ marginTop: 30 }}>操作记录</h3>
                        <Table
                            style={{ marginTop: 25 }}
                            rowKey="id"
                            columns={columnsss}
                            dataSource={orderLogs}
                            pagination={false}
                            scroll={{ y: 800 }}
                        />
                    </div>
                    <Modal
                        title={this.state.modalType == 'audit' ? '审核' : '撤销'}
                        visible={this.state.reviewVisible}
                        onOk={this.handleOk}
                        onCancel={() => {
                            this.setState({ reviewVisible: false, fileList: [] })
                            this.formRefz.current.resetFields()
                        }}
                    >
                        <Form ref={this.formRefz} wrapperCol={16} labelWrap={8}>
                            {this.state.modalType == 'audit' && <Form.Item
                                name="auitResult"
                                label="审核意见"
                                rules={[{ required: true, message: '请输入' }]}
                            >
                                <Radio.Group onChange={(e) => {
                                    this.setState({
                                        auitResult: e.target.value
                                    })
                                }}>
                                    <Radio value={1}>确认</Radio>
                                    <Radio value={2}>驳回</Radio>
                                </Radio.Group>
                            </Form.Item>}
                            <Form.Item
                                name="files"
                                label={this.state.auitResult == 1 ? '支付凭证' : '文件'}
                            >
                                <Upload
                                    action="/guzhe/file/upload"
                                    fileList={this.state.fileList}
                                    onChange={this.handleUploadChange('fileList')}
                                    beforeUpload={(file) => {
                                        const allowedTypes = ['image/jpeg', 'image/png', 'image/jpg', 'application/pdf', 'application/msword', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', 'application/vnd.ms-excel', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', 'text/plain'];
                                        const allowedExtensions = ['.jpg', '.png', '.jpeg', '.pdf', '.doc', '.docx', '.xlsx', '.xls', '.txt'];
                                        const fileExtension = file.name.substring(file.name.lastIndexOf('.')).toLowerCase();

                                        const isAllowedType = allowedTypes.includes(file.type) || allowedExtensions.includes(fileExtension);
                                        if (!isAllowedType) {
                                            message.error('不支持的文件类型，请上传 .jpg .png .jpeg .pdf .doc .docx .xlsx .xls .txt 格式的文件');
                                            return Upload.LIST_IGNORE;
                                        }

                                        return true;
                                    }}
                                    headers={{ token: localStorage.getItem('token') }}
                                >
                                    <Button icon={<UploadOutlined />}>上传文件</Button>
                                    <div
                                        style={{
                                            position: 'relative',
                                            color: 'rgba(0, 0, 0, 0.427450980392157)',
                                        }}
                                    >
                                        <span>支持扩展名：.jpg .png .jpeg .pdf .doc .docx .xlsx .xls .txt</span>
                                    </div>
                                </Upload>
                            </Form.Item>
                            <Form.Item
                                label={this.state.modalType == 'cancel' ? '撤销原因' : this.state.auitResult == 1 ? '审核意见' : '驳回原因'}
                                name="auditOpinion"
                                rules={[{ required: this.state.auitResult == 2, message: '请输入' }]}
                            >
                                <TextArea rows={4} placeholder="请输入" />
                            </Form.Item>
                        </Form>
                    </Modal>
                </Spin>
            </PageContainer>
        );
    }
}

export default orderDetails;
