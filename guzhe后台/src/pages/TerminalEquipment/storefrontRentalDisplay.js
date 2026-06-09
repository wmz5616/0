import { getToken } from '@/utils/authority';
import { urlName } from '@/utils/utils';
import { PageContainer } from '@ant-design/pro-layout';
import {
    Button,
    Col,
    DatePicker,
    Form,
    Input,
    InputNumber,
    message,
    Modal,
    Popconfirm,
    Radio,
    Row,
    Select,
    Spin,
    Switch,
    Table,
    Tooltip,
    Upload,
} from 'antd';
import React from 'react';
// 将connect导入
import { post } from '@/utils/request';
import { history } from '@umijs/max';
import CKEditor from 'react-ckeditor-wrapper';
import dayjs from 'dayjs';
const { RangePicker } = DatePicker;
// 报名管理
const { Option } = Select;
const layout = {
    labelCol: { span: 8 },
    wrapperCol: { span: 10 },
};
const layouts = {
    labelCol: { span: 5 },
    wrapperCol: { span: 10 },
};
class storefrontRentalDisplay extends React.Component {
    formRef = React.createRef();

    state = {
        spinning: false,
        list: [],
        selectedRowKeys: [],
        circleList: [],
    };

    componentDidMount() {
        this.setState({
            time: dayjs().startOf('month').format('YYYY-MM'),
        },()=>{
            this.getAdminList();
            this.getData();
        });
    }

    getAdminList = async () => {
        const res = await post(`/guzhe/common/supermarket/lists`, {
            searchIntStatus: 1,
        });
        if (res && res.code == 10000) {
            this.setState({
                circleList: res.data || [],
            });
        } else {
            message.error(res?.msg);
        }
    };

    getData = () => {
        this.setState(
            {
                spinning: true,
            },
            async () => {
                //列表
                const res = await post(`/guzhe/equipment/rental/display/lists`, {
                    businessCircleId: this.state.searchStrField1,
                    serialNumber: this.state.keyword,
                    month: this.state.time,
                });
                this.setState({
                    spinning: false,
                });
                if (res && res.code == 10000) {
                    this.setState({
                        list: res.data || [],
                    });
                } else {
                    message.error(res?.msg);
                }
            },
        );
    };

    //查询
    onFinish = (vas) => {
        this.setState(
            {
                searchStrField1: vas.searchStrField1,
                keyword: vas.keyword,
                time: vas.time?.format('YYYY-MM') || undefined,
                pageNum: 1,
            },
            () => {
                this.getData();
            },
        );
    };

    //重置
    resets = (vas) => {
        this.formRef.current.resetFields();
        this.setState(
            {
                pageNum: 1,
                searchStrField1: undefined,
                keyword: undefined,
                time: dayjs().startOf('month').format('YYYY-MM'),
                selectedRowKeys: [],
            },
            () => {
                this.getData();
            },
        );
    };

    onSelectChange = (selectedRowKeys) => {
        //触发表单筛选
        this.setState({ selectedRowKeys });
    };

    render() {
        const { list = [], selectedRowKeys } = this.state;

        const rowSelection = {
            selectedRowKeys,
            onChange: this.onSelectChange,
        };

        const columns = [
                     {
                title: '备注',
                dataIndex: 'remark',
            },
            {
                title: '设备编号',
                dataIndex: 'serialNumber',
            },
            {
                title: '所属商超',
                width: 200,
                dataIndex: 'businessCircleName',
            },
            {
                title: '剩余店位',
                dataIndex: 'remainingCount',
            },
            {
                title: '店位商家',
                dataIndex: 'merchantNames',
            },
        ];
        
        return (
            <Spin spinning={this.state.spinning}>
                <PageContainer
                    header={{
                        title: ``,
                    }}
                >
                    <div
                        style={{
                            backgroundColor: '#fff',
                            padding: '20px 20px 0 20px',
                            marginBottom: 15,
                        }}
                    >
                        <Form ref={this.formRef} onFinish={this.onFinish}>
                            <Row gutter={16}>
                                <Col className="gutter-row" span={4}>
                                    <Form.Item label="时间" name="time" initialValue={dayjs().startOf('month')}>
                                        <DatePicker picker="month" style={{width: '100%'}}/>
                                    </Form.Item>
                                </Col>
                                <Col className="gutter-row" span={5}>
                                    <Form.Item label="设备编号" name="keyword">
                                        <Input placeholder="请输入" />
                                    </Form.Item>
                                </Col>
                                <Col className="gutter-row" span={5}>
                                    <Form.Item label="所属商超" name="searchStrField1">
                                        <Select
                                            showSearch
                                            placeholder="请选择"
                                            optionFilterProp="children"
                                        >
                                            {this.state.circleList.map((sa) => (
                                                <Option value={sa.id}>{sa.name}</Option>
                                            ))}
                                        </Select>
                                    </Form.Item>
                                </Col>
                                <Col
                                    className="gutter-row"
                                    style={{ textAlign: 'right', flex: '1 0 220px' }}
                                >
                                    <Form.Item>
                                        <Button type="primary" htmlType="submit">
                                            查询
                                        </Button>

                                        <Button className="mL15" onClick={this.resets}>
                                            重置
                                        </Button>
                                    </Form.Item>
                                </Col>
                            </Row>
                        </Form>
                    </div>

                    <div
                        style={{
                            backgroundColor: '#fff',
                            padding: 20,
                            minHeight: window.innerHeight - 280,
                        }}
                    >
                        <Row>
                            <Col span={12}>
                                <h1 style={{ fontWeight: '600', fontSize: '18px' }}>
                                    设备店位租用展示
                                </h1>
                            </Col>
                        </Row>

                        <Table
                            style={{ marginTop: 15 }}
                            columns={columns}
                            className="csdivcenter"
                            rowKey="id"
                            // rowSelection={rowSelection}
                            rowSelection={false}
                            dataSource={list}
                            pagination={false}
                            scroll={{ x: 'max-content' }}
                        />
                    </div>
                </PageContainer>
            </Spin>
        );
    }
}

export default storefrontRentalDisplay;
