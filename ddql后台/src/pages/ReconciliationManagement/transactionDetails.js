import {
  exportTransactionDetail,
  getTransactionFlowList,
} from '@/services/transaction';
import { PageContainer } from '@ant-design/pro-layout';
import {
  Button,
  Col,
  DatePicker,
  Dropdown,
  Form,
  Input,
  message,
  Row,
  Select,
  Table,
  Typography,
} from 'antd';
import dayjs from 'dayjs';
import { useEffect, useState } from 'react';
import { useLocation } from 'umi';

const { Option } = Select;
const { RangePicker } = DatePicker;
const { Text } = Typography;

const TransactionDetails = () => {
  const [form] = Form.useForm();
  const location = useLocation();
  const [selectedRowKeys, setSelectedRowKeys] = useState([]);

  const initialState = location.state || {};
  const [searchParams, setSearchParams] = useState({
    startTime:
      initialState.startTime ||
      dayjs().startOf('month').format('YYYY-MM-DD 00:00:00'),
    endTime:
      initialState.endTime ||
      dayjs().endOf('day').format('YYYY-MM-DD 23:59:59'),
    pageNum: 1,
    pageSize: 10,
  });

  const [listData, setListData] = useState([]);
  const [totalCount, setTotalCount] = useState(0);
  const [loading, setLoading] = useState(false);

  const loadData = async (params) => {
    setLoading(true);
    try {
      const res = await getTransactionFlowList(params);
      if (res.code === 10000 || res.code === 200) {
        setListData(res.data?.list || []);
        setTotalCount(res.data?.total || 0);
      }
    } catch (error) {
      console.error('加载明细数据失败:', error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (initialState && initialState.startTime && initialState.endTime) {
      const timeRange = [
        dayjs(initialState.startTime),
        dayjs(initialState.endTime),
      ];
      form.setFieldsValue({ time: timeRange });
      const newParams = {
        ...searchParams,
        startTime: initialState.startTime,
        endTime: initialState.endTime,
        pageNum: 1,
      };
      setSearchParams(newParams);
      loadData(newParams);
    } else {
      loadData(searchParams);
    }
  }, [location.state]);

  const list = listData;
  const total = totalCount;

  const onFinish = (values) => {
    const { time, ...rest } = values;
    const newParams = {
      ...rest,
      startTime: time ? time[0].format('YYYY-MM-DD 00:00:00') : undefined,
      endTime: time ? time[1].format('YYYY-MM-DD 23:59:59') : undefined,
      pageNum: 1,
      pageSize: 10,
    };
    setSearchParams(newParams);
    loadData(newParams);
  };

  const handleReset = () => {
    form.resetFields();
    setSelectedRowKeys([]);
    const defaultParams = {
      startTime: dayjs().startOf('month').format('YYYY-MM-DD 00:00:00'),
      endTime: dayjs().endOf('day').format('YYYY-MM-DD 23:59:59'),
      pageNum: 1,
      pageSize: 10,
    };
    setSearchParams(defaultParams);
    loadData(defaultParams);
  };

  const handleExport = (type) => {
    const params = { ...searchParams };
    if (type === 'selected') {
      if (selectedRowKeys.length === 0) return;
      params.searchStrList = selectedRowKeys;
    }
    exportTransactionDetail(params).then((res) => {
      if (!(res instanceof Blob)) {
        message.error(res?.msg || '导出失败，无权限访问');
        return;
      }
      const blob = new Blob([res], { type: 'application/vnd.ms-excel' });
      const objectUrl = URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = objectUrl;
      const timeStr = dayjs().format('YYYYMMDDHHmmss');
      a.download = `交易明细_${timeStr}.xlsx`;
      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);
      URL.revokeObjectURL(objectUrl);
    });
  };

  const columns = [
    {
      title: '时间',
      dataIndex: 'transactionTime',
      key: 'transactionTime',
      width: 170,
    },
    {
      title: '订单号',
      dataIndex: 'orderNo',
      key: 'orderNo',
      width: 180,
    },
    {
      title: '商品',
      dataIndex: 'productName',
      key: 'productName',
      width: 220,
      ellipsis: true,
      render: (text) => text || '-',
    },
    {
      title: '总数量',
      dataIndex: 'totalQuantity',
      key: 'totalQuantity',
      width: 90,
    },
    {
      title: '类型',
      dataIndex: 'type',
      key: 'type',
      width: 80,
      render: (text) => (
        <Text type={text === '收款' ? 'success' : 'danger'}>{text}</Text>
      ),
    },
    {
      title: '金额',
      dataIndex: 'income',
      key: 'income',
      align: 'right',
      width: 120,
      render: (val, record) => {
        const isRefund = record.type === '退款';
        const amount = isRefund
          ? -Math.abs(record.expense || 0)
          : Math.abs(val || 0);
        return (
          <Text type={isRefund ? 'danger' : 'success'}>
            ￥{Number(amount).toFixed(2)}
          </Text>
        );
      },
    },
    {
      title: '手续费',
      dataIndex: 'serviceFee',
      key: 'serviceFee',
      align: 'right',
      width: 100,
      render: (val) => `￥${Number(val || 0).toFixed(2)}`,
    },
    {
      title: '下单人',
      dataIndex: 'orderUser',
      key: 'orderUser',
      width: 180,
      render: (text, record) => {
        if (!text) return '-';
        return record.orderUserPhone ? `${text}(${record.orderUserPhone})` : text;
      },
    },
    {
      title: '备注',
      dataIndex: 'remark',
      key: 'remark',
      width: 220,
      ellipsis: true,
      render: (text) => text || '-',
    },
    {
      title: '商户请求号',
      dataIndex: 'merchantOrderNo',
      key: 'merchantOrderNo',
      width: 200,
    },
    {
      title: '易宝订单号',
      dataIndex: 'yibaoOrderNo',
      key: 'yibaoOrderNo',
      width: 200,
    },
    {
      title: '原收款订单商户请求号',
      dataIndex: 'originMerchantOrderNo',
      key: 'originMerchantOrderNo',
      width: 220,
    },
    {
      title: '原收款订单易宝订单号',
      dataIndex: 'originOrderno',
      key: 'originOrderno',
      width: 220,
    },
  ];

  const menuProps = {
    items: [
      { key: 'all', label: '导出全部', onClick: () => handleExport('all') },
      {
        key: 'selected',
        label: '导出选中',
        onClick: () => handleExport('selected'),
      },
    ],
  };

  return (
    <PageContainer title={false}>
      <div
        style={{
          backgroundColor: '#fff',
          padding: '20px 20px 0 20px',
          marginBottom: 15,
        }}
      >
        <Form form={form} onFinish={onFinish}>
          <Row gutter={16}>
            <Col className="gutter-row" span={5}>
              <Form.Item
                label="时间"
                name="time"
                initialValue={[dayjs().startOf('month'), dayjs()]}
              >
                <RangePicker style={{ width: '100%' }} format="YYYY-MM-DD" />
              </Form.Item>
            </Col>
            <Col className="gutter-row" span={4}>
              <Form.Item label="订单号" name="orderNo">
                <Input placeholder="请输入" allowClear />
              </Form.Item>
            </Col>
            <Col className="gutter-row" span={4}>
              <Form.Item label="商品名称" name="productName">
                <Input placeholder="请输入" allowClear />
              </Form.Item>
            </Col>
            <Col className="gutter-row" span={3}>
              <Form.Item label="类型" name="type">
                <Select
                  placeholder="请选择"
                  allowClear
                  style={{ width: '100%' }}
                >
                  <Option value={1}>收款</Option>
                  <Option value={3}>退款</Option>
                </Select>
              </Form.Item>
            </Col>
            <Col className="gutter-row" span={4}>
              <Form.Item label="下单人" name="orderUser">
                <Input placeholder="请输入" allowClear />
              </Form.Item>
            </Col>
            <Col className="gutter-row" span={4}>
              <Form.Item label="商户请求号" name="merchantOrderNo">
                <Input placeholder="请输入" allowClear />
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
                <Button className="mL15" onClick={handleReset}>
                  重置
                </Button>
              </Form.Item>
            </Col>
          </Row>
        </Form>
      </div>

      <div style={{ backgroundColor: '#fff', padding: 20 }}>
        <Row gutter={16}>
          <Col span={6}>
            <h1 style={{ fontWeight: '600', fontSize: '18px' }}>交易明细</h1>
          </Col>
          <Col
            span={18}
            style={{ display: 'flex', justifyContent: 'flex-end', gap: 10 }}
          >
            <Dropdown menu={menuProps} placement="bottomRight">
              <Button variant="outlined">导出</Button>
            </Dropdown>
          </Col>
        </Row>

        <Table
          style={{ marginTop: 15 }}
          rowSelection={{
            selectedRowKeys,
            onChange: setSelectedRowKeys,
          }}
          rowKey="id"
          columns={columns}
          dataSource={listData}
          pagination={{
            showSizeChanger: true,
            pageSize: searchParams.pageSize,
            total: totalCount,
            current: searchParams.pageNum,
            onChange: (page, pageSize) => {
              const newParams = { ...searchParams, pageNum: page, pageSize };
              setSearchParams(newParams);
              loadData(newParams);
            },
          }}
          loading={loading}
          scroll={{ x: 'max-content' }}
        />
      </div>
    </PageContainer>
  );
};

export default TransactionDetails;
