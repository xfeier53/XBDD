import { makeStyles, createStyles } from '@material-ui/core';

export const useSummaryStyles = makeStyles(() =>
  createStyles({
    summaryTitle: {
      color: 'inherit',
      paddingTop: '20px',
      paddingBottom: '16px',
    },
    productListContainer: {
      overflow: 'auto',
      padding: '24px',
    },
  })
);
