FROM nginx:stable
RUN rm /etc/nginx/conf.d/default.conf
COPY app /usr/share/nginx/html
COPY conf /etc/nginx