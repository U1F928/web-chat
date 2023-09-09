FROM node:18-slim AS build_stage

WORKDIR app

# Allow for the caching of dependencies
COPY app/package-lock.json .
COPY app/package.json .
RUN npm install

COPY app/ .

RUN npm run build

FROM nginx:stable
RUN rm /etc/nginx/conf.d/default.conf
COPY --from=build_stage app/dist /usr/share/nginx/html
COPY conf /etc/nginx
