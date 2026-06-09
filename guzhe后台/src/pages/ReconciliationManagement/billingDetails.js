import {
  exportSubLedgerDetail,
  getSubLedgerDetailList,
  getSubLedgerSummary,
} from '@/services/transaction';
import { PageContainer } from '@ant-design/pro-layout';
import {
  Alert,
  Button,
  Col,
  DatePicker,
  Form,
  Input,
  message,
  Row,
  Space,
  Table,
  Typography,
} from 'antd';
import dayjs from 'dayjs';
import { useEffect, useState } from 'react';
import { useLocation } from 'umi';

const { RangePicker } = DatePicker;
const { Text } = Typography;

const BillingDetails = () => {
  const [form] = Form.useForm();
  const location = useLocation();
  const [searchParams, setSearchParams] = useState({
    startTime:
      location.state?.startTime ||
      dayjs().startOf('month').format('YYYY-MM-DD 00:00:00'),
    endTime:
      location.state?.endTime ||
      dayjs().endOf('day').format('YYYY-MM-DD 23:59:59'),
    pageNum: 1,
    pageSize: 10,
  });

  const [listData, setListData] = useState([]);
  const [totalCount, setTotalCount] = useState(0);
  const [summaryInfo, setSummaryInfo] = useState({});
  const [loading, setLoading] = useState(false);

  const loadData = async (params) => {
    setLoading(true);
    try {
      const [listRes, summaryRes] = await Promise.all([
        getSubLedgerDetailList(params),
        getSubLedgerSummary(params),
      ]);

      if (listRes.code === 10000 || listRes.code === 200) {
        setListData(listRes.data?.list || []);
        setTotalCount(listRes.data?.total || 0);
      }
      if (summaryRes.code === 10000 || summaryRes.code === 200) {
        setSummaryInfo(summaryRes.data || {});
      }
    } catch (error) {
      console.error('加载数据失败:', error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    const params = { ...searchParams };
    loadData(params);

    if (location.state?.startTime) {
      form.setFieldsValue({
        time: [dayjs(location.state.startTime), dayjs(location.state.endTime)],
      });
    }
  }, []); // eslint-disable-line react-hooks/exhaustive-deps

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
    const defaultParams = {
      startTime: dayjs().startOf('month').format('YYYY-MM-DD 00:00:00'),
      endTime: dayjs().endOf('day').format('YYYY-MM-DD 23:59:59'),
      pageNum: 1,
      pageSize: 10,
    };
    setSearchParams(defaultParams);
    loadData(defaultParams);
  };

  const handleExport = () => {
    exportSubLedgerDetail(searchParams).then((res) => {
      if (!(res instanceof Blob)) {
        message.error(res?.msg || '导出失败，无权限访问');
        return;
      }
      const blob = new Blob([res], { type: 'application/vnd.ms-excel' });
      const objectUrl = URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = objectUrl;
      a.download = `分账明细_${dayjs().format('YYYYMMDDHHmmss')}.xlsx`;
      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);
      URL.revokeObjectURL(objectUrl);
    });
  };

  const columns = [
    {
      title: '分账时间',
      dataIndex: 'transactionTime',
      key: 'transactionTime',
      width: 170,
    },
    {
      title: '分账金额 (元)',
      dataIndex: 'divideAmount',
      key: 'divideAmount',
      width: 130,
      render: (val) => (
        <span style={{ color: val < 0 ? '#ff4d4f' : 'inherit' }}>
          {Number(val).toFixed(2)}
        </span>
      ),
    },
    {
      title: '通莞手续费 (元)',
      dataIndex: 'tongGuanFee',
      key: 'tongGuanFee',
      width: 130,
      render: (val) => Number(val).toFixed(2),
    },
    {
      title: '平台收费 (元)',
      dataIndex: 'platformFee',
      key: 'platformFee',
      width: 130,
      render: (val) => Number(val).toFixed(2),
    },
    {
      title: '订单金额 (元)',
      dataIndex: 'orderAmount',
      key: 'orderAmount',
      width: 130,
      render: (val) => (
        <span style={{ color: val < 0 ? '#ff4d4f' : 'inherit' }}>
          {Number(val).toFixed(2)}
        </span>
      ),
    },
    {
      title: '订单号',
      dataIndex: 'orderNo',
      key: 'orderNo',
      width: 200,
    },
    {
      title: '分账请求号',
      dataIndex: 'subLedgerRequestNo',
      key: 'subLedgerRequestNo',
      width: 200,
      ellipsis: true,
    },
  ];

  const summary = summaryInfo;

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
                label="分账时间"
                name="time"
                initialValue={[dayjs().startOf('month'), dayjs()]}
              >
                <RangePicker format="YYYY-MM-DD" style={{ width: '100%' }} />
              </Form.Item>
            </Col>
            <Col className="gutter-row" span={4}>
              <Form.Item label="订单号" name="orderNo">
                <Input placeholder="请输入" allowClear />
              </Form.Item>
            </Col>
            <Col className="gutter-row" span={4}>
              <Form.Item label="分账请求号" name="subLedgerRequestNo">
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
                <Button
                  className="mL15"
                  onClick={handleReset}
                  style={{ marginLeft: 8 }}
                >
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
            <h1 style={{ fontWeight: '600', fontSize: '18px' }}>分账明细</h1>
          </Col>
          <Col
            span={18}
            style={{ display: 'flex', justifyContent: 'flex-end', gap: 10 }}
          >
            <Button type="primary" onClick={handleExport}>
              导出
            </Button>
          </Col>
          <Col span={24} style={{ margin: '10px 0' }}>
            <Alert
              message={
                <Space split={<span>|</span>} size="middle">
                  <span>
                    查询统计：共{' '}
                    <Text strong style={{ color: '#ff8800' }}>
                      {summary.totalCount || 0}
                    </Text>{' '}
                    笔
                  </span>
                  <span>
                    分账金额{' '}
                    <Text strong style={{ color: '#ff8800' }}>
                      ¥{Number(summary.totalDivideAmount || 0).toFixed(2)}
                    </Text>
                  </span>
                  <span>
                    通莞手续费{' '}
                    <Text strong style={{ color: '#ff8800' }}>
                      ¥{Number(summary.totalTongGuanFee || 0).toFixed(2)}
                    </Text>
                  </span>
                  <span>
                    平台收费{' '}
                    <Text strong style={{ color: '#ff8800' }}>
                      ¥{Number(summary.totalPlatformFee || 0).toFixed(2)}
                    </Text>
                  </span>
                </Space>
              }
              type="info"
              showIcon
            />
          </Col>
        </Row>

        <Table
          style={{ marginTop: 15 }}
          rowKey={(record, index) => record.subLedgerRequestNo + index}
          columns={columns}
          dataSource={listData}
          pagination={{
            showSizeChanger: false,
            pageSize: searchParams.pageSize,
            total: totalCount,
            current: searchParams.pageNum,
            onChange: (page, pageSize) => {
              const newParams = {
                ...searchParams,
                pageNum: page,
                pageSize: pageSize || searchParams.pageSize,
              };
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

export default BillingDetails;
