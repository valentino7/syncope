/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.syncope.client.console.pages;

import static org.apache.wicket.Component.ENABLE;

import de.agilecoders.wicket.extensions.markup.html.bootstrap.behavior.Draggable;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.behavior.DraggableConfig;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.behavior.Resizable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.syncope.client.console.SyncopeConsoleSession;
import org.apache.syncope.client.console.panels.EditRealmModalPanel;
import org.apache.syncope.client.console.panels.Realm;
import org.apache.syncope.client.console.panels.RealmModalPanel;
import org.apache.syncope.client.console.rest.RealmRestClient;
import org.apache.syncope.client.console.wicket.ajax.markup.html.ClearIndicatingAjaxLink;
import org.apache.syncope.client.console.wicket.markup.html.bootstrap.buttons.DefaultModalCloseButton;
import org.apache.syncope.client.console.wicket.markup.html.bootstrap.dialog.BaseModal;
import org.apache.syncope.common.lib.to.RealmTO;
import org.apache.syncope.common.lib.types.Entitlement;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.authroles.authorization.strategies.role.metadata.MetaDataRoleAuthorizationStrategy;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow.WindowClosedCallback;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

public class Realms extends BasePage {

    private static final long serialVersionUID = -1100228004207271270L;

    @SpringBean
    private RealmRestClient realmRestClient;

    private final WebMarkupContainer content;

    protected RealmTO currentRealm;

    public Realms(final PageParameters parameters) {
        super(parameters);

        final List<RealmTO> realms = realmRestClient.list();
        Collections.sort(realms, new RealmNameComparator());

        addRealmTree(getParentMap(realms), 0L, Realms.this);
        setCurrentRealm(realms.get(0));

        content = new WebMarkupContainer("content");
        content.add(new Label("header", "Root realm"));
        content.add(new Label("body", "Root realm"));
        content.setOutputMarkupId(true);
        updateRealmContent(currentRealm);
        add(content);
    }

    private MarkupContainer addRealmTree(
            final Map<Long, List<RealmTO>> parentMap, final Long key, final MarkupContainer container) {
        final RepeatingView listItems = new RepeatingView("list");
        listItems.setOutputMarkupId(true);
        container.addOrReplace(listItems);

        for (final RealmTO realm : parentMap.get(key)) {
            final Fragment fragment;

            final AjaxLink<Void> link = new AjaxLink<Void>("link") {

                private static final long serialVersionUID = -7978723352517770644L;

                @Override
                public void onClick(final AjaxRequestTarget target) {
                    Realms.this.setCurrentRealm(realm);
                    send(Realms.this, Broadcast.EXACT, new ControlSidebarClick<>(realm, target));
                }
            };

            link.addOrReplace(new Label("name", new PropertyModel<String>(realm, "name")));

            if (parentMap.containsKey(realm.getKey()) && !parentMap.get(realm.getKey()).isEmpty()) {
                fragment = new Fragment(String.valueOf(realm.getKey()), "withChildren", Realms.this);
                addRealmTree(parentMap, realm.getKey(), fragment);
            } else {
                fragment = new Fragment(String.valueOf(realm.getKey()), "withoutChildren", Realms.this);
            }

            fragment.addOrReplace(link);
            fragment.setOutputMarkupId(true);
            listItems.addOrReplace(fragment);
        }

        return container;
    }

    private Map<Long, List<RealmTO>> getParentMap(final List<RealmTO> realms) {
        final Map<Long, List<RealmTO>> res = new HashMap<>();
        res.put(0L, new ArrayList<RealmTO>());

        final Map<Long, List<RealmTO>> cache = new HashMap<>();

        for (RealmTO realm : realms) {
            if (res.containsKey(realm.getParent())) {
                res.get(realm.getParent()).add(realm);

                final List<RealmTO> children = new ArrayList<>();
                res.put(realm.getKey(), children);

                if (cache.containsKey(realm.getKey())) {
                    children.addAll(cache.get(realm.getKey()));
                    cache.remove(realm.getKey());
                }
            } else if (cache.containsKey(realm.getParent())) {
                cache.get(realm.getParent()).add(realm);
            } else {
                final List<RealmTO> children = new ArrayList<>();
                children.add(realm);
                cache.put(realm.getParent(), children);
            }
        }

        return res;
    }

    private static class RealmNameComparator implements Comparator<RealmTO>, Serializable {

        private static final long serialVersionUID = 7085057398406518811L;

        @Override
        public int compare(final RealmTO r1, final RealmTO r2) {
            return r1.getName().compareTo(r2.getName());
        }
    }

    @Override
    public void onEvent(final IEvent<?> event) {
        super.onEvent(event);

        if (event.getPayload() instanceof ControlSidebarClick) {
            @SuppressWarnings("unchecked")
            final ControlSidebarClick<RealmTO> controlSidebarClick = ControlSidebarClick.class.cast(event.getPayload());
            updateRealmContent(controlSidebarClick.getObj());
            controlSidebarClick.getTarget().add(content);
        }
    }

    private void setCurrentRealm(final RealmTO realmTO) {
        this.currentRealm = realmTO;
    }

    public RealmTO getCurrentRealm() {
        return this.currentRealm;
    }

    private void updateRealmContent(final RealmTO realmTO) {
        content.addOrReplace(new Label("header", realmTO.getName()));
        content.addOrReplace(new Realm("body", realmTO, getPageReference()));
        setupCreateModal();
        setupEditModal();
    }

    private void setupCreateModal() {
        final BaseModal<RealmTO> createModal = new BaseModal<>("createModal");
        createModal.add(new Resizable().withChildSelector(".modal-content"));
        createModal.add(new Draggable(new DraggableConfig().withHandle(".modal-header").withCursor("move")));
        createModal.addButton(new DefaultModalCloseButton());
        createModal.header(new ResourceModel("createRealm"));
        createModal.setUseKeyboard(true);
        createModal.setFadeIn(true);

        createModal.setWindowClosedCallback(new WindowClosedCallback() {

            private static final long serialVersionUID = 8804221891699487139L;

            @Override
            public void onClose(final AjaxRequestTarget target) {
                final List<RealmTO> realms = realmRestClient.list();
                Collections.sort(realms, new RealmNameComparator());
                target.add(addRealmTree(getParentMap(realms), 0L, Realms.this));
            }
        });

        content.addOrReplace(createModal);

        final RealmModalPanel realmModalPanel = new RealmModalPanel(BaseModal.getModalContentId(),
                this.getPageReference(), createModal,
                new RealmTO(), this.getCurrentRealm().getFullPath(), Entitlement.REALM_CREATE);

        realmModalPanel.setOutputMarkupId(true);
        createModal.addOrReplace(realmModalPanel);

        //create new realms                
        final AjaxLink<Void> createLink = new ClearIndicatingAjaxLink<Void>("createLink", getPageReference()) {

            private static final long serialVersionUID = -7978723352517770644L;

            @Override
            protected void onClickInternal(final AjaxRequestTarget target) {
                createModal.addOrReplace(realmModalPanel);
                createModal.show(target);
            }

        };
        if (SyncopeConsoleSession.get().owns(Entitlement.REALM_CREATE)) {
            MetaDataRoleAuthorizationStrategy.authorize(createLink, ENABLE, Entitlement.REALM_CREATE);
        }
        content.addOrReplace(createLink);
    }

    private void setupEditModal() {

        final BaseModal<RealmTO> editModal = new BaseModal<>("editModal");
        editModal.add(new Resizable().withChildSelector(".modal-content"));
        editModal.add(new Draggable(new DraggableConfig().withHandle(".modal-header").withCursor("move")));
        editModal.addButton(new DefaultModalCloseButton());
        editModal.header(Model.of(getCurrentRealm().getName()));
        editModal.setUseKeyboard(true);
        editModal.setFadeIn(true);

        editModal.setWindowClosedCallback(new WindowClosedCallback() {

            private static final long serialVersionUID = 8804221891699487139L;

            @Override
            public void onClose(final AjaxRequestTarget target) {
                final List<RealmTO> realms = realmRestClient.list();
                Collections.sort(realms, new RealmNameComparator());
                target.add(addRealmTree(getParentMap(realms), 0L, Realms.this));
            }
        });

        content.addOrReplace(editModal);

        final RealmModalPanel ediRealmModalPanel =
                new EditRealmModalPanel(BaseModal.getModalContentId(),
                        Realms.this.getPageReference(),
                        editModal, getCurrentRealm(), Realms.this.getCurrentRealm().getFullPath(),
                        Entitlement.REALM_UPDATE);

        ediRealmModalPanel.setOutputMarkupId(true);
        editModal.addOrReplace(ediRealmModalPanel);

        final AjaxLink<Void> edit = new ClearIndicatingAjaxLink<Void>("edit", getPageReference()) {

            private static final long serialVersionUID = -6957616042924610290L;

            @Override
            protected void onClickInternal(final AjaxRequestTarget target) {
                editModal.show(target);
            }
        };
        content.addOrReplace(edit);
    }

    private static class ControlSidebarClick<T> {

        private final AjaxRequestTarget target;

        private final T obj;

        public ControlSidebarClick(
                final T obj, final AjaxRequestTarget target) {
            this.obj = obj;
            this.target = target;
        }

        public T getObj() {
            return obj;
        }

        public AjaxRequestTarget getTarget() {
            return target;
        }
    }
}
