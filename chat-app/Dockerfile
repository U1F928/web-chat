FROM node:slim AS build_stage
COPY app app
WORKDIR app
RUN npm install
RUN npm run build

FROM nginx:stable
RUN rm /etc/nginx/conf.d/default.conf
COPY --from=build_stage app/dist /usr/share/nginx/html
COPY conf /etc/nginx