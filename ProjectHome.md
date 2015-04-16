Different standalone java tools need to gather statistics of their usage (for example that given feature was
used, that user clicked on something, saved some file, etc.). Eclipse Usage Data Collector (UDC) provides
such functionality but has no official API and it is tightly connected with Eclipse RCP, so the more general
Java framework for usage statistic collection is needed.