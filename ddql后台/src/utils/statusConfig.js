export const getOrderStatusInfo = (status) => {
  const config = {
    1: { text: '排队中', color: '#6a90ff' },
    2: { text: '服务中', color: '#000' },
    3: { text: '待支付', color: '#f59a24' },
    4: { text: '已完成', color: '#5cdabb' },
    5: { text: '退款中', color: '#d9011c' },
    6: { text: '已退款', color: '#000' },
    7: { text: '已取消', color: '#c8c8c8' },
    default: { text: '已过号', color: '#000' },
  };
  return config[status] || config.default;
};
